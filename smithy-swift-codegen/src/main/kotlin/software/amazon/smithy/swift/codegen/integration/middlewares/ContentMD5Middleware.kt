package software.amazon.smithy.swift.codegen.integration.middlewares

import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.OperationShape
import software.amazon.smithy.model.traits.HttpChecksumRequiredTrait
import software.amazon.smithy.swift.codegen.ClientRuntimeTypes
import software.amazon.smithy.swift.codegen.ServiceGenerator
import software.amazon.smithy.swift.codegen.SwiftWriter
import software.amazon.smithy.swift.codegen.middleware.MiddlewarePosition
import software.amazon.smithy.swift.codegen.middleware.MiddlewareRenderable
import software.amazon.smithy.swift.codegen.middleware.MiddlewareRenderableExecutionContext
import software.amazon.smithy.swift.codegen.middleware.MiddlewareStep
import software.amazon.smithy.swift.codegen.model.hasTrait

class ContentMD5Middleware : MiddlewareRenderable {
    override val name = "ContentMD5Middleware"

    override val middlewareStep = MiddlewareStep.BUILDSTEP

    override val position = MiddlewarePosition.BEFORE

    override fun render(model: Model, symbolProvider: SymbolProvider, writer: SwiftWriter, op: OperationShape, operationStackName: String, executionContext: MiddlewareRenderableExecutionContext) {
        if (op.hasTrait<HttpChecksumRequiredTrait>()) {
            val outputShapeName = ServiceGenerator.getOperationOutputShapeName(symbolProvider, model, op)
            val outputErrorName = ServiceGenerator.getOperationErrorShapeName(op)
            writer.write("$operationStackName.${middlewareStep.stringValue()}.intercept(position: ${position.stringValue()}, middleware: \$N<$outputShapeName, $outputErrorName>())", ClientRuntimeTypes.Middleware.ContentMD5Middleware)
        }
    }
}
