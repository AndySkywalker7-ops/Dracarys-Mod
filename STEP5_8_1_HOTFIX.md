# STEP 5.8.1 — sideSign compile hotfix

GitHub Actions failed at `:compileJava` because `BalancedDragonModel` calls `sideSign(boolean)` in `createForeleg`, `createHindleg`, and `createFoot`, but that helper was not defined.

Fix added:

```java
private static float sideSign(boolean left) {
    return left ? 1.0F : -1.0F;
}
```

No renderer, gameplay, multipart, tracking, stage, size, or long-range rendering code is changed.

Suggested commit:

`Fix missing sideSign helper in BalancedDragonModel`
