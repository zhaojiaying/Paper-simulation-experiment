/**
 * Ƥ��ѷ���ϵ��
 */
public class PearsonCorrelationScore {
    private double[] xData;
    private double[] yData;
    private double xMeans;
    private double yMeans;
    /**
     * ���Ƥ��ѷ�ķ���
     */
    private double numerator;
    /**
     * ���Ƥ��ѷϵ���ķ�ĸ
     */
    private double denominator;
    /**
     * ��������Ƥ��ѷϵ��
     */
    private double pearsonCorrelationScore;

    public PearsonCorrelationScore(double[] truth, double[] self_report) {
        //�Ȼ�ȡDataNode������
        this.xData = truth;
        this.yData = self_report;
        //�õ��������ݵ�ƽ��ֵ
        this.xMeans = this.getMeans(xData);
        this.yMeans = this.getMeans(yData);
        //����Ƥ��ѷϵ���ķ���
        this.numerator = this.generateNumerator();
        //����Ƥ��ѷϵ���ķ�ĸ
        this.denominator = this.generateDenomiator();
        //����Ƥ��ѷϵ��
        this.pearsonCorrelationScore = this.numerator / this.denominator;
    }

    /**
     * ���ɷ���
     * @return ����
     */
    private double generateNumerator() {
        double sum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            sum += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return sum;
    }
    /**
     * ���ɷ�ĸ
     * @return ��ĸ
     */
    private double generateDenomiator() {
        double xSum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }

        double ySum = 0.0;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }

        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    /**
     * ���ݸ��������ݼ�����ƽ��ֵ����
     * @param datas ���ݼ�
     * @return �������ݼ���ƽ��ֵ
     */
    private double getMeans(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }

    public double getPearsonCorrelationScore() {
        return this.pearsonCorrelationScore;
    }
}