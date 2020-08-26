locals {
  local_env                     = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  preview_vault_name            = "${var.raw_product}-aat"
  non_preview_vault_name        = "${var.raw_product}-${var.env}"
  key_vault_name                = "${var.env == "preview" || var.env == "spreview" ? local.preview_vault_name : local.non_preview_vault_name}"
}

data "azurerm_key_vault" "rd_key_vault" {
  name                = "${local.key_vault_name}"
  resource_group_name = "${local.key_vault_name}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name          = "${var.component}-POSTGRES-PASS"
  value         = "${module.db-rd-location-ref-api.postgresql_password}"
  key_vault_id  = "${data.azurerm_key_vault.rd_key_vault.id}"
}

module "db-rd-location-ref-api" {
  source              = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product             = "${var.product}-${var.component}-postgres-db"
  location            = "${var.location}"
  subscription        = "${var.subscription}"
  env                 = "${var.env}"
  postgresql_user     = "dbrdlocationref"
  database_name       = "dbrdlocationref"
  common_tags         = "${var.common_tags}"
  postgresql_version  = "${var.postgresql_version}"
}