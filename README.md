# proxinius

A FullContact API proxy service that randomly mutates responses for
chaos testing.

## Getting started

#### Most basic setup:
- Clone this repo
- Change `base-url` in dev.yaml to the api you want to test
- Run the repl - `./lein repl`
- Start proxinius - `(api/-main)`
- Proxinius is now running on port `8984`. You should change the
  request base in your app to IP of the device proxinius is running
  on. This usually looks something like `192.168.1.16:8984` (the IP address can be
  found using `ifconfig` or `ip addr` on more modern Linux distros)
- You have achieved chaos! By default proxinius randomly mutates 10%
  of requests passing through it. You can change this in dev.yaml

### Mutating a specific request
proxinius currently works by:

- Creating a matcher for a request
- Defining a mutation to apply for a specific matcher

---

An example to match a specific request goes like this in the repl:

```clojure
(defn match-my-request
  [{:keys [uri]}]
  (= uri "/v1/users"))

(defn internal-server-error
  [request]
  (go-try (mutations/internal-server-error request @api/requests)))

(api/add-request-mutation match-my-request internal-server-error)

```
The example above will return a 500 for all requests to `/v1/users`.

---

You can also use `user/add-route-mutation` to match requests
using [clout](https://github.com/weavejester/clout) routing
pattern. For example:

```clojure
(add-route-mutation "/v1/users/:user-id" do-random-mutation>)
```

will match all requests that conform to `"/v1/users/:user-id"`, and
apply random mutation.

---

**NB!** matcher-mutation pairs are saved in a set, so a single request
may not trigger one matcher but fall through to another. proxinius
registers the random matcher right now on start-up. If not disabled
this may lead to unexpected mutations.

You can clean all of the existing mutations using:
```clojure
(api/remove-request-mutations)
```

![](https://media.giphy.com/media/ixblGFHml3TyM/giphy.gif)
