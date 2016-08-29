package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
 * consultor
 * 
 * @author aghu
 *
 */
public class RelatorioEstatisticaProdutividadeConsultorVO {

	private List<ItemRelatorioEstatisticaProdutividadeConsultorVO> resultados;
	private String tempoMedioGeralConhecimento;
	private String tempoMedioGeralResposta;

	public RelatorioEstatisticaProdutividadeConsultorVO() {
		this.resultados = new ArrayList<ItemRelatorioEstatisticaProdutividadeConsultorVO>();
	}

	public RelatorioEstatisticaProdutividadeConsultorVO(List<ItemRelatorioEstatisticaProdutividadeConsultorVO> resultados, String tempoMedioGeralConhecimento, String tempoMedioGeralResposta) {
		super();
		this.resultados = resultados;
		this.tempoMedioGeralConhecimento = tempoMedioGeralConhecimento;
		this.tempoMedioGeralResposta = tempoMedioGeralResposta;
	}

	public List<ItemRelatorioEstatisticaProdutividadeConsultorVO> getResultados() {
		return resultados;
	}

	public void setResultados(List<ItemRelatorioEstatisticaProdutividadeConsultorVO> resultados) {
		this.resultados = resultados;
	}

	public String getTempoMedioGeralConhecimento() {
		return tempoMedioGeralConhecimento;
	}

	public void setTempoMedioGeralConhecimento(String tempoMedioGeralConhecimento) {
		this.tempoMedioGeralConhecimento = tempoMedioGeralConhecimento;
	}

	public String getTempoMedioGeralResposta() {
		return tempoMedioGeralResposta;
	}

	public void setTempoMedioGeralResposta(String tempoMedioGeralResposta) {
		this.tempoMedioGeralResposta = tempoMedioGeralResposta;
	}

}
