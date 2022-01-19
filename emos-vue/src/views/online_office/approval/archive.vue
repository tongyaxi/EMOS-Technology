<template>
	<el-dialog title="アーカイブ実施" width="500px" :close-on-click-modal="false" v-model="visible" :show-close="false">
		<el-upload
			ref="upload"
			:action="url"
			list-type="picture-card"
			accept=".jpg,.jpeg,.png"
			with-credentials="true"
			:before-upload="beforeUploadHandle"
			:on-success="successHandle"
			:on-remove="removeHandle"
		>
			<i class="el-icon-plus"></i>
		</el-upload>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="cancel()">キャンセル</el-button>
				<el-button type="primary" @click="archive()" size="medium" :disabled="disableBtn">{{ btn }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			url: this.$baseUrl + 'cos/uploadCosFile?type=archive',
			taskId: '',
			picList: {},
			disableBtn: false,
			btn: '実施'
		};
	},
	methods: {
		init: function(taskId) {
		    let _this = this;
		    _this.visible = true;
		    _this.taskId = taskId;
		    _this.btn = 'アーカイブ実施';
		    _this.disableBtn = false;
		    _this.$nextTick(() => {
		        _this.$refs['upload'].clearFiles();
		    });
		},
		beforeUploadHandle: function(file) {
		    if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png') {
		        this.$message.error('jpg、png以外のイメージをサポートされません。');
		        return false;
		    }
		    return true;
		},
		successHandle: function(resp, file, fileList) {
		    if (resp && resp.code === 200) {
		        for (let one of fileList) {
		            this.picList[one.response.url] = one.response.path;
		        }
		    } else {
		        this.$message.error('アップロードに失敗しました。');
		    }
		},
		removeHandle: function(file, fileList) {
		    
			let _this = this;
		    let url = file.response.url;
		    let path = this.picList[url];
		    _this.$http('cos/deleteCosFile', 'POST', { pathes: [path] }, true, function(resp) {
		        delete _this.picList[url];
		    });
		},
		cancel: function() {
		    
			let _this = this;
		    if (Object.keys(_this.picList).length > 0) {
		        
				let pathes = Object.values(_this.picList);
		        _this.$http('cos/deleteCosFile', 'POST', { pathes: pathes }, true, function(resp) {
		            _this.picList = {};
		        });
		    }
		    _this.visible = false;
		    _this.$refs['upload'].clearFiles();
		},
		// アーカイブ
		archive: function(){
			let _this = this;
			_this.btn = "アーカイブ中";
			_this.disableBtn = true;
			
			if(Object.keys(_this.picList).length == 0){
				_this.$message({
					message: '画像をアップロードしてください。',
					type: 'error',
					duration: 1200
				});
				return;
			}
			
			let files = [];
			for(let key in _this.picList){
				files.push({
					url: key,
					path: _this.picList[key]
				});
			}
			
			let reqData = {
				taskId: _this.taskId,
				files: JSON.stringify(files)
			}
			_this.$http("approval/archiveTask", "POST", reqData, true, function(resp){
				_this.$message({
					message: 'アーカイブを実施しました。',
					type: 'success',
					duration: 1200
				});
				_this.visible = false;
				_this.$emit('refreshDataList');
			});
		}
	}
};
</script>

<style lang="less"></style>
