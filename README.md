# Hookify
**Webhook 처리 라이브러리**
다양한 플랫폼에서 Webhook 이벤트를 간편하게 관리하고 처리할 수 있는 라이브러리 입니다.</br>
Slack, GitHub, 결제 시스템(Stripe, PayPal) 등 다양한 서비스와 통합 가능하며, Webhook 이벤트 검증,</br>
로그 기록, 실패 재시도와 같은 기능을 제공합니다.</br>

---

## 프로젝트 개요
### 주요 특징
- **Webhook 요청 검증**: 서명(Signiture) 및 타임 스탬프(Timestamp) 검증으로 안전한 Webhook 처리.
- **로그 기록**: Webhook 요청 성공/실패 상태를 로깅하여 디버깅과 추적 가능.
- **재시도 기능**: 실패한 Webhook 요청을 설정된 규칙에 따라 재시도.
- **플러그인 가능**: Slack, GitHub 등 다양한 플랫폼의 Webhook 처리를 위한 플러그인 지원.
- **사용자 정의 핸들러**: 사용자가 정의한 이벤트 핸들러를 등록하여 유연한 처리 가능.

---
📌 커밋 메시지 작성 규칙
이 프로젝트는 Semantic Versioning에 따라 버전이 자동으로 관리됩니다.
따라서 아래와 같은 커밋 메시지 규칙을 따라주세요:

커밋 메시지 형식
```text
<type>: <short description>

<optional body>

<optional footer>

```

타입(Type)
커밋의 목적을 나타냅니다. 다음 타입을 사용해주세요:

feat: 새로운 기능 추가 (버전 MINOR 증가)<br>
fix: 버그 수정 (버전 PATCH 증가)<br>
chore: 빌드, 설정 변경 등 코드에 영향이 없는 변경<br>
refactor: 코드 리팩토링 (기능 변경 없음)<br>
docs: 문서 변경 (README.md 등)<br>
test: 테스트 코드 추가 및 수정<br>
style: 코드 스타일 변경 (공백, 포맷 등)<br>
perf: 성능 개선<br>
ci: CI/CD 설정 및 스크립트 변경<br>

예시
**기능 추가(feat)**:
```text
feat: Add webhook handler registry

- 사용자 정의 핸들러를 등록할 수 있는 레지스트리 기능 추가
- Map<String, Handler> 기반으로 이벤트 타입과 핸들러 매핑

```

**버그 수정(fix)**:
```text
fix: Resolve signature validation issue

- Signature 비교 시 인코딩 문제 해결
- GitHub WebhookValidator 검증 로직 수정

```

**코드 리팩토링(refactor)**:
```text
refactor: Simplify WebhookPipeline validation flow

- 중복 코드 제거 및 구조 개선


```

**문서 수정(docs)**:
```text
docs: Add commit message guidelines to README

- Semantic release를 위해 커밋 메시지 규칙 추가
- feat, fix 등 타입에 대한 설명 포함
```

**BREAKING CHANGES**:
기존 기능을 호환되지 않게 변경하는 경우, **BREAKING CHANGE**를 커밋 메시지 본문에 추가해야 합니다.
```text
feat: Update WebhookValidator interface

BREAKING CHANGE: validate() method now requires 'eventType' parameter

```
---


## 📦 설치 방법
#### 1. Gradle 설치 예제

```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
    implementation 'com.github.hyuk12:Hookify:Tag'
}
```

#### 2. Maven 설치 예제
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
    <groupId>com.github.hyuk12</groupId>
    <artifactId>Hookify</artifactId>
    <version>Tag</version>
</dependency>
```

---

## 🚀 사용 방법

### 1. 순수 Java 사용 예제
- Webhook 요청 검증과 로깅 기능을 사용하는 예제

  ```java
  public class HookifyExample {
    public static void main(String[] args) {
        String secret = "your-webhook-secret";
        String eventType = "push";
        String signature = "sha256=valid-signature";
        String payload = "{ \"ref\": \"refs/heads/main\" }";

        WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);

        try {
            pipeline.execute(eventType, signature, null, payload);
            System.out.println("Webhook successfully processed!");
        } catch (Exception e) {
            System.err.println("Failed to process webhook: " + e.getMessage());
        }
    }
  ```

### 2. Spring Boot 사용 예제
  - `application.properties` 설정
    ```yml
      hookify.webhook.secret=your-webhook-secret
    ```
  - Webhook Controller
    ```java
      @RestController
      @RequestMapping("/github")
      public class WebhookController {
          private final WebhookPipeline pipeline;
      
          public WebhookController(WebhookPipeline pipeline) {
              this.pipeline = pipeline;
          }
      
          @PostMapping("/webhook")
          public ResponseEntity<String> handleGitHubWebhook(
              @RequestHeader("X-GitHub-Event") String eventType,
              @RequestHeader("X-Hub-Signature-256") String signature,
              @RequestBody String payload) {
      
              pipeline.execute(eventType, signature, null, payload);
              return ResponseEntity.ok("Webhook processed successfully!");
          }
      }
    ```
---

## 🎥 데모 (추후 추가)

추후 동영상 또는 animated GIF로 Webhook 요청 처리 과정을 설명할 예정입니다.

