variable "product" {
  type = "string"
}

variable "component" {
  type = "string"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "subscription" {
  type = "string"
}

// variable "deployment_namespace" {}

variable "common_tags" {
  type = "map"
}

variable "postgresql_version" {
  type    = "string"
  default = "11"
}