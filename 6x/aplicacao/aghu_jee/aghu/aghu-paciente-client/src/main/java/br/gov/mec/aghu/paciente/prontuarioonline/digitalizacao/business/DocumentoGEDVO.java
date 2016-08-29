package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class DocumentoGEDVO implements Serializable {

	private static final String prontuario = "prontuario", PRONTUARIO = "Prontuário", PRONTUARIO1 = "Prontuario",
								tipo = "tipo", TIPO = "Tipo",
								origem = "origem", ORIGEM = "Origem",
								data = "data", DATA = "Data",
								mes = "mes", mes1 = "mês", MES = "Mês", MES1 = "Mes",
								ano = "ano", ANO = "Ano",
								TRACO = " - ";
	
	
	private static final long serialVersionUID = -945928276484214579L;

	private Integer ficha;
	private Map<String, Object> camposFicha;
	private String urlAcesso;

	public DocumentoGEDVO() {
	}

	public DocumentoGEDVO(Integer ficha, Map<String, Object> camposFicha, String urlAcesso) {
		super();
		this.ficha = ficha;
		this.camposFicha = camposFicha;
		this.urlAcesso = urlAcesso;
	}

	public String getNome() {
		Map<String, String> decodedMap = decodeMap();

		StringBuilder nome = montaNomeVisualizacao(decodedMap);
		return nome.toString();
	}

	private Map<String, String> decodeMap() {
		Map<String, String> decodeMap = new HashMap<String, String>();
		for (Iterator<String> iterator = camposFicha.keySet().iterator(); iterator.hasNext();) {
			String nomeCampo = iterator.next();
			buscaCampoProntuarioPorNome(decodeMap, nomeCampo);
			buscaCampoTipoPorNome(decodeMap, nomeCampo);
			buscaCampoOrigemPorNome(decodeMap, nomeCampo);
			buscaCampoDataPorNome(decodeMap, nomeCampo);
			buscaCampoMesPorNome(decodeMap, nomeCampo);
			buscaCampoAnoPorNome(decodeMap, nomeCampo);
		}
		return decodeMap;
	}

	private void buscaCampoProntuarioPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo) && (nomeCampo.contains(PRONTUARIO) || nomeCampo.contains(PRONTUARIO1))) {
			decodeMap.put(prontuario, (String) camposFicha.get(nomeCampo));
		}
	}

	private void buscaCampoTipoPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo) && (nomeCampo.contains(tipo) || nomeCampo.contains(TIPO))) {
			decodeMap.put(tipo, (String) camposFicha.get(nomeCampo));
		}
	}

	private void buscaCampoOrigemPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo) && (nomeCampo.contains(origem) || nomeCampo.contains(ORIGEM))) {
			decodeMap.put(origem, (String) camposFicha.get(nomeCampo));
		}
	}

	private void buscaCampoDataPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo) && (nomeCampo.contains(data) || nomeCampo.contains(DATA))) {
			decodeMap.put(data, (String) camposFicha.get(nomeCampo));
		}
	}

	private void buscaCampoMesPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo)
				&& (nomeCampo.contains(mes) || nomeCampo.contains(MES1) || nomeCampo.contains(MES) || nomeCampo.contains(mes1))) {
			decodeMap.put(mes, (String) camposFicha.get(nomeCampo));
		}
	}

	private void buscaCampoAnoPorNome(Map<String, String> decodeMap, String nomeCampo) {
		if (StringUtils.isNotBlank(nomeCampo) && (nomeCampo.contains(ano) || nomeCampo.contains(ANO))) {
			decodeMap.put(ano, (String) camposFicha.get(nomeCampo));
		}
	}

	private StringBuilder montaNomeVisualizacao(Map<String, String> decodedMap) {
		StringBuilder nome = new StringBuilder();
		if (StringUtils.isNotBlank(decodedMap.get(data))) {
			nome.append(decodedMap.get(data)).append(TRACO);
		}
		if (StringUtils.isNotBlank(decodedMap.get(mes)) && StringUtils.isNotBlank(decodedMap.get(ano))) {
			nome.append(decodedMap.get(mes)).append('/').append(decodedMap.get(ano)).append(TRACO);
		}
		if (StringUtils.isNotBlank(decodedMap.get(origem))) {
			nome.append(decodedMap.get(origem)).append(TRACO);
		}
		if (StringUtils.isNotBlank(decodedMap.get(tipo))) {
			nome.append(decodedMap.get(tipo)).append(TRACO);
		}
		if (StringUtils.isNotBlank(decodedMap.get(prontuario))) {
			nome.append(decodedMap.get(prontuario));
		}
		return nome;
	}

	public String getUrlAcesso() {
		return urlAcesso;
	}

	public void setUrlAcesso(String urlAcesso) {
		this.urlAcesso = urlAcesso;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DocumentoGEDVO) {
			return ((DocumentoGEDVO) obj).getUrlAcesso().equals(this.getUrlAcesso());
		}
		return super.equals(obj);
	}

	public Integer getFicha() {
		return ficha;
	}

	public void setFicha(Integer ficha) {
		this.ficha = ficha;
	}

	public Map<String, Object> getCamposFicha() {
		return camposFicha;
	}

	public void setCamposFicha(Map<String, Object> camposFicha) {
		this.camposFicha = camposFicha;
	}
}
