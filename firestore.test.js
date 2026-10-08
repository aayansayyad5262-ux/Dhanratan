const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: "127.0.0.1",
      port: 8085,
      rules: fs.readFileSync("firestore.rules", "utf8"),
    },
  });
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test("Unauthenticated user cannot read or write user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc("user_1").get());
  await assertFails(
    unauthDb.collection("users").doc("user_1").set({
      userId: "user_1",
      fullName: "Test Player",
      username: "test@example.com",
      phone: "9876543210",
      balance: 0,
      createdAt: new Date(),
    })
  );
});

test("Authenticated user can create, read, and update their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext("alice_uid").firestore();
  const createdAt = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc("alice_uid").set({
      userId: "alice_uid",
      fullName: "Alice Sharma",
      username: "alice@example.com",
      phone: "9876543210",
      balance: 500,
      createdAt: createdAt,
      updatedAt: createdAt,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc("alice_uid").get());

  await assertSucceeds(
    aliceDb.collection("users").doc("alice_uid").update({
      fullName: "Alice R. Sharma",
      balance: 1200,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Authenticated user cannot read or write another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext("alice_uid").firestore();
  const bobDb = testEnv.authenticatedContext("bob_uid").firestore();
  const createdAt = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc("alice_uid").set({
      userId: "alice_uid",
      fullName: "Alice Sharma",
      username: "alice@example.com",
      phone: "9876543210",
      balance: 500,
      createdAt: createdAt,
    })
  );

  await assertFails(bobDb.collection("users").doc("alice_uid").get());
  await assertFails(
    bobDb.collection("users").doc("alice_uid").update({
      balance: 0,
    })
  );
});
