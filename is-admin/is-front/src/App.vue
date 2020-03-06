<template>
  <div id="app">
    <template v-if="authenticated">
    <img alt="Vue logo" src="./assets/logo.png">
    <div style="font-size: 30px">Hello MicroServer !</div>
    <Button @click="getOrder()">Get Order</Button>
    <p>order id : {{order.id}}</p>
    <p>order product id : {{order.productId}}</p>
    <Button @click="logout()">logout</Button>
    </template>
    <template v-else>
    <Row>
      <Col span="8" offset="8">
        <template>
          <Form ref="credentials" :model="credentials" :rules="ruleInline">
            <FormItem prop="username">
              <Input type="text" v-model="credentials.username" placeholder="Username">
                <Icon type="ios-person-outline" slot="prepend"></Icon>
              </Input>
            </FormItem>
            <FormItem prop="password">
              <Input type="password" v-model="credentials.password" placeholder="Password">
                <Icon type="ios-lock-outline" slot="prepend"></Icon>
              </Input>
            </FormItem>
            <FormItem>
              <Button type="primary" @click="handleSubmit('credentials')">login</Button>
            </FormItem>
          </Form>
        </template>
      </Col>
    </Row>
    </template>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import axios from 'axios'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  data () {
    return {
      authenticated:false,
      credentials: {
        username: '',
        password: ''
      },
      order:{},
      ruleInline: {
        username: [
          { required: true, message: 'Please fill in the user name', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please fill in the password.', trigger: 'blur' },
          { type: 'string', min: 6, message: 'The password length cannot be less than 6 bits', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    logout(){
      axios.post('logout').then(()=>{
        this.authenticated = false;
      }).catch(()=>{

      });
    },
    getOrder(){
      axios.get('api/order/orders/1').then((response)=>{
        this.order = response.data;
        console.log(this.order);
      }).catch(()=>{
        this.$Message.error('get order Fail!');
      });
    },
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          axios.post('login',this.credentials).then(()=>{
            this.authenticated = true;
            this.$Message.success('Success!');
          }).catch(()=>{
            this.$Message.error('Fail!');
          });
        } else {
          this.$Message.error('Fail!');
        }
      })
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
