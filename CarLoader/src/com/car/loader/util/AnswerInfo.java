package com.car.loader.util;

public class AnswerInfo {
	private AnswerInfo(){
		
	}
	/**
	 * ����״̬���жϳ������쳣��Ϣ�������ı���ʾ
	 * @param t ����״̬��Ϣ��
	 * @return
	 */
	public static String answerInfo(int t){
		StringBuffer buffer = new StringBuffer();
		//System.out.println("t======"+t+"7&16="+(7&16));
		if ((t & 1) == 1) {
			buffer.append("����������20%���뼰ʱ����Ŷ\n");
		}
		if ((t & 2) == 2) {
			buffer.append("����15000KM���뼰ʱ��������Ŷ\n");
		}
		if ((t & 4) == 4) {
			buffer.append("�������쳣���뼰ʱά��Ŷ\n");
		}
		if ((t & 8) == 8) {
			buffer.append("�ƶ����쳣���뼰ʱά��Ŷ\n");
		}
		if ((t & 16) == 16) {
			//System.out.println("zhi-------------------------");
			buffer.append("�����쳣���뼰ʱά��Ŷ\n");
		}
		
		return buffer.toString();
	}
}
