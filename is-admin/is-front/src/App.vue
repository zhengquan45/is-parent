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
  mounted(){
    this.init();
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
    async init(){
      const res = await axios.get('me');
      if(res.data){
        this.authenticated = true
      }
      if(!this.authenticated){
        window.location.href = 'http://auth.zhq.com:9090/oauth/authorize?' +
                'client_id=admin&' +
                'redirect_url=http://admin.zhq.com:8080/oauth/callback&' +
                'response_type=code&' +
                'state=adc';
      }
    },
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
