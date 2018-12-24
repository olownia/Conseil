package tech.cryptonomic.conseil.generic.chain

import com.typesafe.config.Config
import tech.cryptonomic.conseil.generic.chain.PlatformDiscoveryTypes.{Network, Platform}

import scala.collection.JavaConverters._

object NetworkConfigOperations {
  /**
    * Extracts networks from config file
    *
    * @param  config configuration object
    * @return list of networks from configuration
    */
  def getNetworks(config: Config): List[Network] = {
    for {
      (platform, strippedConf) <- config.getObject("platforms").asScala
      (network, _) <- strippedConf.atKey(platform).getObject(platform).asScala
    } yield Network(network, network.capitalize, platform, network)
  }.toList


  /**
    * Extracts platforms from config file
    *
    * @param  config configuration object
    * @return list of platforms from configuration
    */
  def getPlatforms(config: Config): List[Platform] = {
    for {
      (platform, _) <- config.getObject("platforms").asScala
    } yield Platform(platform, platform.capitalize)
  }.toList
}