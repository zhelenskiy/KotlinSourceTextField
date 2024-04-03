//
//  ContentView.swift
//  sampleIos
//
//  Created by Evgeniy.Zhelenskiy on 3/28/24.
//

import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    
    private func safeAreaPaddingRadius() -> Float {
        return (UIScreen.main.value(forKey: "_displayCornerRadius") as? CGFloat).map { Float($0) } ?? 0.0
    }
    
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Note: Update view controller if needed.
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all) // Compose has own keyboard handler
    }
}

#Preview {
    ContentView()
}
