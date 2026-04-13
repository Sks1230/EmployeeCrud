import os
from groq import Groq

client = Groq(api_key=os.environ.get("GROQ_API_KEY"))

print("Groq Chat (type 'exit' to quit)\n")
while True:
    user_input = input("You: ")
    if user_input.lower() == "exit":
        break
    response = client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[{"role": "user", "content": user_input}]
    )
    print(f"AI: {response.choices[0].message.content}\n")
