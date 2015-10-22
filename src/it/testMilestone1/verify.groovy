def props = new Properties()

new File(basedir,"target/classes/test.properties").withInputStream { 
  stream -> props.load(stream) 
}

assert "0.5" == props["phase"];