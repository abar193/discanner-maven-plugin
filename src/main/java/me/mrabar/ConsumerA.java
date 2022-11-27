package me.mrabar;

import me.mrabar.versions.ChangedClass;

public class ConsumerA {
  public void consoom() {
    ChangedClass c = new ChangedClass();
    c.a();
    System.out.println(c.b());
  }

  public static void consum() {
    ConsumerA ca = new ConsumerA();
    ca.consoom();
  }
}
