import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.subscribe.nativebridge.annotation.Event
import com.subscribe.nativebridge.annotation.Method
import com.subscribe.nativebridge.annotation.MethodReturn
import com.subscribe.nativebridge.annotation.Module
import com.subscribe.nativebridge.annotation.Param
import com.subscribe.nativebridge.annotation.Return
import com.subscribe.nativebridge.event.EventHandler
import com.subscribe.nativebridge.event.EventHandlerBase
import com.subscribe.nativebridge.method.MethodHandlerBase
import com.subscribe.nativebridge.modules.BridgeModule
import com.subscribe.nativebridge.modules.BridgeModuleCenter
import com.subscribe.nativebridge.modules.BridgeModuleProvider
import com.subscribe.nativebridge.modules.impl.BridgeModuleError


class JSBridgeProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {

    companion object {
        const val MODULE_PREFIX = "JSBridgeModule"
        const val FACTORY_NAME = "JSBridgeModuleFactory"
        private val MUTABLE_MAP_CLASS = ClassName("kotlin.collections", "MutableMap")
        private val EVENT_HANDLE_CLASS = ClassName(
            EventHandlerBase::class.java.packageName,
            "${EventHandlerBase::class.simpleName}"
        )
        private val EVENT_HANDLE_GENERIC_CLASS =
            EVENT_HANDLE_CLASS.parameterizedBy(TypeVariableName.invoke("*"))
    }

    private var invoked = false
    private var moduleFileSpecList: MutableList<FileSpec> = mutableListOf()
    private var pkgName = BridgeModuleCenter::class.java.packageName

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true

        val symbols = resolver.getSymbolsWithAnnotation(Module::class.qualifiedName!!)
        logger.info(symbols.toList().toString())
        symbols.filter { it is KSClassDeclaration }.map { it to (it as KSClassDeclaration) }
            .forEach { entry ->
                val ksClass = entry.first
                val ksClassDec = entry.second
                val pkgName = ksClassDec.declarations.first().packageName.asString()
                val className = ksClass.toString()

                /**
                 * 模块名称
                 */
                val jsModule = ksClass.getAnnotationsByType(Module::class).firstOrNull()
                    ?: return@forEach
                logger.info("jsModule: [${jsModule.name}]")
                val fileSpec = FileSpec.builder(this.pkgName, "${MODULE_PREFIX}${jsModule.name}")
                    .addImport(pkgName, className)
                    .addImport(MethodReturn::class.java.kotlin, "")
                    .addImport("com.subscribe.nativebridge", "fromPBArray")
                    .addImport("com.subscribe.nativebridge", "toPBArray")
                val classSpec = TypeSpec.objectBuilder("${MODULE_PREFIX}${jsModule.name}")
                    .addModifiers(KModifier.INTERNAL).addSuperinterface(BridgeModule::class)
                // 属性module
                val module =
                    PropertySpec.builder(BridgeModule::module.name, String::class, KModifier.OVERRIDE)
                        .initializer("%S", jsModule.name)
                // 属性enableSendThread
                val enableSend =
                    PropertySpec.builder(BridgeModule::enableSendThread.name, Boolean::class, KModifier.OVERRIDE)
                        .initializer("${jsModule.enableSendThread}")
                // 属性enableRecvThread
                val enableRecv =PropertySpec.builder(BridgeModule::enableRecvThread.name, Boolean::class, KModifier.OVERRIDE)
                    .initializer("${jsModule.enableRecvThread}")
                // 属性methodHandlers
                val methodHandlers = PropertySpec.builder(
                    BridgeModule::methodHandlers.name, MUTABLE_MAP_CLASS.parameterizedBy(String::class.asTypeName(), MethodHandlerBase::class.asTypeName()), KModifier.OVERRIDE
                ).initializer("mutableMapOf()")
                // 属性eventHandlers
                val eventHandlers = PropertySpec.builder(
                    BridgeModule::eventHandlers.name,
                    MUTABLE_MAP_CLASS.parameterizedBy(
                        String::class.asTypeName(), EVENT_HANDLE_GENERIC_CLASS
                    ),
                    KModifier.OVERRIDE
                ).initializer("mutableMapOf()")
                // 方法initModule
                val initModule = FunSpec.builder(BridgeModule::initModule.name)
                    .addModifiers(KModifier.OVERRIDE)

                // 模块方法
                ksClassDec.declarations.filter { it is KSFunctionDeclaration }
                    .map { it as KSFunctionDeclaration }.forEach { kfun ->
                        generateMethodCode(fileSpec, kfun, jsModule, initModule, ksClass)
                    }

                // 模块事件
                val eventFunSpecList: MutableList<FunSpec.Builder> = mutableListOf()
                ksClassDec.declarations.filter { it is KSClassDeclaration }.forEach { eventClass ->
                    generateEventCode(eventClass, jsModule, initModule, ksClass, eventFunSpecList)
                }

                // 初始化
                initModule.addCode(
                    """
                        |
                        |${BridgeModule::methodHandlers.name}.forEach { it.value.${MethodHandlerBase::setMethod.name}(${BridgeModule::module.name}).${MethodHandlerBase::setMethod.name}(it.key) }
                        |${BridgeModule::eventHandlers.name}.forEach { it.value.${EventHandlerBase<*>::setMethod.name}(${BridgeModule::module.name}).${EventHandlerBase<*>::setMethod.name}(it.key) }
                        |
                        |""".trimMargin()
                )

                classSpec.addProperty(module.build())
                classSpec.addProperty(enableSend.build())
                classSpec.addProperty(enableRecv.build())
                classSpec.addProperty(methodHandlers.build())
                classSpec.addProperty(eventHandlers.build())
                classSpec.addFunction(initModule.build())
                fileSpec.addType(classSpec.build())

                eventFunSpecList.forEach {
                    fileSpec.addFunction(it.build())
                }
                moduleFileSpecList.add(fileSpec.build())
            }
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun generateEventCode(
        eventClass: KSDeclaration,
        jsModule: Module,
        initModule: FunSpec.Builder,
        ksClass: KSAnnotated,
        eventFunSpecList: MutableList<FunSpec.Builder>
    ) {
        val jsEvent = eventClass.getAnnotationsByType(Event::class).firstOrNull()
            ?: return
        val jsEventGenericType =
            (eventClass as KSClassDeclaration).superTypes.firstOrNull()?.element?.typeArguments?.firstOrNull()?.type?.resolve()

        logger.info("jsEvent: [${jsModule.name}-${jsEvent.name}]")
        initModule.addCode(
            """
                                |// Event: ${jsEvent.name}
                                |${BridgeModule::eventHandlers.name}["${jsEvent.name}"] = $ksClass.${eventClass.simpleName.getShortName()}
                                |
                                |""".trimMargin()
        )

        // 事件扩展方法
        val eventClassName = ClassName(
            eventClass.packageName.asString(),
            "$ksClass.${eventClass.simpleName.getShortName()}"
        )
        val eventGenericName = ClassName(
            jsEventGenericType!!.declaration.packageName.asString(),
            jsEventGenericType.declaration.simpleName.getShortName()
        ).copy(nullable = jsEventGenericType.isMarkedNullable)

        val sendMethod =
            FunSpec.builder("sendEvent")
                .addParameter("event", eventGenericName)
                .receiver(eventClassName)
        sendMethod.addCode(
            """
                            |this.${EventHandler::send.name}("", ${EventHandler::module.name}, ${EventHandler::method.name}, event.toPBArray()) 
                            |""".trimMargin()
        )
        eventFunSpecList.add(sendMethod)
    }

    @OptIn(KspExperimental::class)
    private fun generateMethodCode(
        fileSpec: FileSpec.Builder,
        kfun: KSFunctionDeclaration,
        jsModule: Module,
        initModule: FunSpec.Builder,
        ksClass: KSAnnotated
    ) {
        val jsMethod = kfun.getAnnotationsByType(Method::class).firstOrNull() ?: return

        val jsParam = kfun.parameters.find { it.getAnnotationsByType(Param::class).any() }
        val jsParamType = jsParam?.type?.resolve()

        val jsReturn = kfun.parameters.find { it.getAnnotationsByType(Return::class).any() }
        val jsReturnType = jsReturn?.type?.resolve()?.declaration?.simpleName?.asString()
        val jsReturnGenericType =
            jsReturn?.type?.element?.typeArguments?.firstOrNull()?.type?.resolve()

        logger.info("jsMethod: [${jsModule.name}-${jsMethod.name}]")
        val paramClassName = ClassName(
            jsParamType!!.declaration.packageName.asString(),
            jsParamType.declaration.simpleName.getShortName()
        )
        val returnClassName = ClassName(
            jsReturnGenericType!!.declaration.packageName.asString(),
            jsReturnGenericType.declaration.simpleName.getShortName()
        )
        fileSpec.addImport(paramClassName, "")
        fileSpec.addImport(returnClassName, "")
        initModule.addCode(
            """
                                |// Method: ${jsMethod.name}
                                |${BridgeModule::methodHandlers.name}["${jsMethod.name}"] = object: ${MethodHandlerBase::class.simpleName}() {
                                |    override fun ${MethodHandlerBase::handle.name}(reqId: String, module: String, method: String, params: ByteArray) {
                                |        val req = params.fromPBArray<${jsParamType}>()
                                |        $ksClass.${kfun.simpleName.getShortName()}(req, object : $jsReturnType<${jsReturnGenericType}> {
                                |           override fun invoke(result: ${jsReturnGenericType}) {
                                |               ${MethodHandlerBase::onMethodReturn.name}(reqId, module, method, result.toPBArray())
                                |           }
                                |        })
                                |    }
                                |}
                                |
                                |""".trimMargin()
        )
    }

    override fun finish() {
        super.finish()
        moduleFileSpecList.forEach {
            writeFileSpec(it)
        }
        writeFileSpec(getModuleFactory(moduleFileSpecList).build())
    }

    private fun writeFileSpec(fileSpec: FileSpec) {
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES, fileSpec.packageName, fileSpec.name
        )
        file.use {
            val content = fileSpec.toString().toByteArray()
            it.write(content)
        }
    }

    @OptIn(DelicateKotlinPoetApi::class)
    private fun getModuleFactory(moduleFileSpecList: MutableList<FileSpec>): FileSpec.Builder {
        val fileSpec = FileSpec.builder(pkgName, FACTORY_NAME)
            .addImport(BridgeModuleError.javaClass.kotlin, "")
        // 类
        val classSpec =
            TypeSpec.objectBuilder(FACTORY_NAME)
                .addModifiers(KModifier.INTERNAL)
                .addSuperinterface(BridgeModuleProvider::class.java)

        // 属性
        val modules = PropertySpec.builder(
            BridgeModuleProvider::modules.name, MUTABLE_MAP_CLASS.parameterizedBy(String::class.asTypeName(), BridgeModule::class.asTypeName()), KModifier.OVERRIDE
        ).initializer("mutableMapOf()")

        // 方法
        val initModules = FunSpec.builder(BridgeModuleProvider::initModules.name)
            .addModifiers(KModifier.OVERRIDE)
        moduleFileSpecList.forEach {
            initModules.addStatement("${it.name}.${BridgeModule::initModule.name}()")
        }
        moduleFileSpecList.forEach {
            initModules.addStatement("${BridgeModuleProvider::modules.name}[${it.name}.${BridgeModule::module.name}] = ${it.name}")
        }

        // 方法

        val moduleNull = ClassName(BridgeModule::class.java.packageName, BridgeModule::class.java.simpleName)
        val getModule = FunSpec.builder(BridgeModuleProvider::getModule.name)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(BridgeModule::module.name, String::class)
            .returns(BridgeModule::class)
            .returns(moduleNull)
            .addStatement("return ${BridgeModuleProvider::modules.name}[${BridgeModule::module.name}] ?: ${BridgeModuleError::class.simpleName}")

        classSpec.addProperty(modules.build())
        classSpec.addFunction(initModules.build())
        classSpec.addFunction(getModule.build())
        fileSpec.addType(classSpec.build())
        return fileSpec
    }

}

class JSBridgeProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return JSBridgeProcessor(environment.codeGenerator, environment.logger)
    }
}
