from flask import Flask, jsonify, request

app = Flask(__name__)

testing_types = [
  { 'name': 'unit testing', 'description': 'testing individual units of source code' }
]

@app.route('/')
def hello():
  return jsonify({"message": "hello"})
  

@app.route('/tests')
def get_tests():
  return jsonify(testing_types)


@app.route('/tests', methods=['POST'])
def add_test():
  testing_types.append(request.get_json())
  return '', 204
  
  
  
if __name__ == '__main__':
    app.run(debug=True)  
    
    
    
    
    

