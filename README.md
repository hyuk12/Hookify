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

---

## 📄 라이선스

```text
MIT License

Copyright (c) 2024 ChoiHaeHyuk

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
