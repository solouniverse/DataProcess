package com.dataprocess.engine

import com.azure.identity.DefaultAzureCredentialBuilder
import com.azure.security.keyvault.secrets.SecretClientBuilder
import com.azure.core.exception.AzureException

object Credentials {
  try {
    // Build credential using default Azure credentials
    val credential = new DefaultAzureCredentialBuilder().build()

    // Connect to Key Vault
    val keyvault_name = "dataray-kv" // Replace with your Key Vault name
    val keyVaultUrl = s"https://$keyvault_name.vault.azure.net/"
    val secretClient = new SecretClientBuilder()
      .vaultUrl(keyVaultUrl)
      .credential(credential)
      .buildClient()

    // Get secrets
    val clientId = secretClient.getSecret("client-id").getValue
    val tenantId = secretClient.getSecret("tenant-id").getValue
    val clientSecret = secretClient.getSecret("client-secret").getValue

    // Log for validation (avoid logging secrets in production!)
    println(s"✅ Successfully retrieved clientId: $clientId")

  } catch {
    case ae: AzureException =>
      System.err.println(s"❌ Azure error occurred: ${ae.getMessage}")
    case ex: Exception =>
      System.err.println(s"❌ Failed to retrieve secrets from Key Vault: ${ex.getMessage}")
  }
}
