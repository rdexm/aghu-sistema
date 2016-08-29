package br.gov.mec.aghu.faturamento.vo;

import java.util.ArrayList;
import java.util.List;

public class FtLogErrorVO implements Comparable<FtLogErrorVO> {

	//descrição do erro
	private String erro;
	
	// Codigos de erro e phi
	private List<FtLogErrorPhiCodVO> ftLogErrorPhiCodVO = new ArrayList<FtLogErrorPhiCodVO>();
	private String criticidade;//situação
	
	// Novo Contrutor
	public FtLogErrorVO(String erro,List<FtLogErrorPhiCodVO> ftLogErrorPhiCodVO ,String criticidade) {
		this.erro=erro;
		this.ftLogErrorPhiCodVO=ftLogErrorPhiCodVO;
		this.criticidade = criticidade;
	}
	

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}
	
	public String getCriticidade() {
		return criticidade;
	}

	public void setCriticidade(String criticidade) {
		this.criticidade= criticidade;
	}
	
	public List<FtLogErrorPhiCodVO> getFtLogErrorPhiCodVO() {
		return ftLogErrorPhiCodVO;
	}


	public void setFtLogErrorPhiCodVO(List<FtLogErrorPhiCodVO> ftLogErrorPhiCodVO) {
		this.ftLogErrorPhiCodVO = ftLogErrorPhiCodVO;
	}


	@Override
	public int compareTo(FtLogErrorVO outroVO) {
		int comparadorOutro=0;
		int comparadorThis=0;
		
		comparadorOutro = atribuiValorParaOrdenacao(outroVO);
		comparadorThis = atribuiValorParaOrdenacao(this);
		
		if (comparadorThis < comparadorOutro) {
            return -1;
        }
        if (comparadorThis > comparadorOutro) {
            return 1;
        }
		return 0;
	}


	private int atribuiValorParaOrdenacao(FtLogErrorVO outroVO) {
		
		if(outroVO.criticidade.equalsIgnoreCase("Não Cobra")){ 
			return 1;
		}else if(outroVO.criticidade.equalsIgnoreCase("Inconsistência")){
			return 2;
		}else if(outroVO.criticidade.equalsIgnoreCase("Não Encerra")){
			return 3;
		}else{
			return 4;
		}
	}

}
