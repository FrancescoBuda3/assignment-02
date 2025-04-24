import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { parse, createVisitor } from 'java-ast'

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  files: File[] = []
  ast = ""

  onFolderSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      for (let i = 0; i < input.files.length; i++) {
        const file = input.files[i]
        this.files.push(file)
      }
    } else {
      console.log('No file selected')
    }
  }

  start() {
    for (let i = 0; i < this.files.length; i++) {
      const file = this.files[i]
      const reader = new FileReader()
      reader.onload = (e) => {
        const content = e.target?.result as string
        this.ast = JSON.stringify(parse(content), null, 2)
      }
      reader.readAsText(file)
    }
  }
}
