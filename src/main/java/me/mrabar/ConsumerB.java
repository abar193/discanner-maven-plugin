package me.mrabar;

import me.mrabar.versions.DeprecatedClass;

public class ConsumerB {
  public void kansume() {
    DeprecatedClass dc = new DeprecatedClass();
    System.out.println(dc.getSomething());
  }

  public static void cnsm() {
    DeprecatedClass dc = new DeprecatedClass();
    System.out.println(dc.getSomething());
  }
}
