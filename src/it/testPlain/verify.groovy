def props = new Properties()

new File(basedir,"target/classes/test.properties").withInputStream { 
  stream -> props.load(stream) 
}

assert "1" == props["phase"];