package com.preachooda.domain

class FileNotDeletedException(path: String) : Exception("File not deleted. Path = $path")
