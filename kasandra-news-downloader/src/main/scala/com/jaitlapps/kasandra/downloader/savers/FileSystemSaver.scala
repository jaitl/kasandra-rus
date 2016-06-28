package com.jaitlapps.kasandra.downloader.savers

import com.jaitlapps.kasandra.downloader.configs.FsSaverConfig

class FileSystemSaver(config: FsSaverConfig) extends Saver {
  override def save(html: String): Unit = ???
}
