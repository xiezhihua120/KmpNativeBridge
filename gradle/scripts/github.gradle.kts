public open class GithubPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("xzh: project ${project.name} apply plugin!")
    }
}

project.apply<GithubPublishPlugin>()