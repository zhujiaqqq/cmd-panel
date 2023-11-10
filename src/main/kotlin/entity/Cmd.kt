package entity

data class Cmd(val name: String, val description: String, val cmd: String, val retry: Int, val timeout: Int)
