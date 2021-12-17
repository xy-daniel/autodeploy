package autodeploy

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
            }
        }

        "/task/exec?/$id?/$deviceId?(.$format)?"(controller: "task", action: "exec")

        "/"(controller: "index")
        "500"(controller: "error")
        "404"(controller: "error")
        "405"(controller: "error")
        "300"(controller: "authority")
    }
}
