""" Base images. """

load("@io_bazel_rules_docker//container:container.bzl", "container_pull")

def base_images():
    """define base images."""

    # https://gcr.io/distroless/java17-debian11:nonroot-amd64
    container_pull(
        name = "java17_amd64",
        architecture = "amd64",
        digest = "sha256:5778252a15df33be03b4e42a7a60053279bfda410a6805e3e3470652255cfdc5",
        os = "linux",
        registry = "gcr.io",
        repository = "distroless/java17-debian11",
        tag = "nonroot-amd64",
    )

    # https://gcr.io/distroless/java17-debian11:nonroot-arm64
    container_pull(
        name = "java17_arm64",
        architecture = "arm64",
        digest = "sha256:9d30decc186154df210bdd46ddede7945049b9e83760cc731e59e71956aff34e",
        os = "linux",
        registry = "gcr.io",
        repository = "distroless/java17-debian11",
        tag = "nonroot-arm64",
    )
