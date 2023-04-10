variable "region" {
  default = "us-east-1"
}

variable "vpc_cidr_block" {
  default = "10.0.0.0/16"
}
variable "subnet_cidr_block" {
  default = "10.0.10.0/24"
}
variable "avail_zone" {
  default = "us-east-1b"
}
variable "env_prefix" {
  default = "dev"
}
variable "my_ip" {
  default = "41.155.1.65"
}
variable "instance_type" {
  default = "t3.micro"
}