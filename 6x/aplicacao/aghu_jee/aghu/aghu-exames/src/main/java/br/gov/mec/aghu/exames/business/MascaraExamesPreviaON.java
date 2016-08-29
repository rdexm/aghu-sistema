package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.laudos.ExameVO;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.MascaraVO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.IAelResultadoExame;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Prévia para a mascara de exames.
 * 
 * @author cvagheti
 *
 */
@Stateless
public class MascaraExamesPreviaON extends BaseBusiness {

	private static final long serialVersionUID = -5604609737001633802L;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesPreviaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public ExamesListaVO buscarDadosLaudo(
			List<AelParametroCamposLaudo> parametrosPrevia,
			AelVersaoLaudo versaoLaudo) throws BaseException {
		ExamesListaVO result = new ExamesListaVO();

		List<ExameVO> exames = new ArrayList<ExameVO>();
		ExameVO exame = this.processaItem(parametrosPrevia, versaoLaudo);
		exames.add(exame);

		result.setExames(exames);

		return result;
	}

	private ExameVO processaItem(
			List<AelParametroCamposLaudo> camposDaVersaoLaudo,
			AelVersaoLaudo versaoLaudo) throws BaseException, ApplicationBusinessException {
		if (versaoLaudo == null) {
			throw new IllegalArgumentException(
					"O parametro versaoLaudo não pode ser nulo.");
		}

		// busca os valores de resultado para cada campo do laudo
		Map<AelParametroCamposLaudo, IAelResultadoExame> resultados = this
				.obterMapaResultadosFicticiosExames(camposDaVersaoLaudo);
		// this
		// .obterMapaResultadosExames(item);

		// adiciona os campos q não tem resultado
		for (AelParametroCamposLaudo campo : camposDaVersaoLaudo) {
			if (!resultados.containsKey(campo)) {
				resultados.put(campo, null);
			}
		}

		// buscar resultado
		Map<AelParametroCamposLaudo, String> c = new HashMap<AelParametroCamposLaudo, String>();
		for (Map.Entry<AelParametroCamposLaudo, IAelResultadoExame> entry : resultados
				.entrySet()) {
			String s = this.extraiResultadoString(entry);
			c.put(entry.getKey(), s);
		}

		Map<AelParametroCamposLaudo, String> listaFinal = c;

		ExameVO exame = new ExameVO();
		exame.setSolicitacao(123456);
		exame.setItem((short) 1);
		exame.setSigla("SGL");
		exame.setDescricao("DESCRIÇÃO DO EXAME");
		exame.setMostraDescricao(true);
		// this.populaMaterialAnaliseRegiaoAnatomica(item, exame);
		exame.setLiberacao(new Date());
		exame.setRecebimento(new Date());

		// cria os VOs com os campos da mascara
		List<MascaraVO> mascaras = new ArrayList<MascaraVO>();
		for (Map.Entry<AelParametroCamposLaudo, String> entry : listaFinal
				.entrySet()) {
			AelParametroCamposLaudo key = entry.getKey();

			MascaraVO.IdVO id = new MascaraVO.IdVO(key.getId()
					.getVelEmaExaSigla(), key.getId().getVelEmaManSeq(), key
					.getId().getVelSeqp(), key.getId().getCalSeq(), key.getId()
					.getSeqp());
			MascaraVO mascara = new MascaraVO(id);

			mascara.setAlinhamento(key.getAlinhamento().name().toString()
					.toCharArray()[0]);
			mascara.setAlturaObjetoVisual(key.getAlturaObjetoVisual());
			mascara.setCor(key.getCor());
			// exibir no relatorio
			boolean exibir1 = false;
			if (key.getExibicao().equals(DominioExibicaoParametroCamposLaudo.R)
					|| key.getExibicao().equals(
							DominioExibicaoParametroCamposLaudo.A)) {
				exibir1 = true;
			}
			mascara.setExibeRelatorio(exibir1);
			
			// exibir na tela
			exibir1 = false;
			if (key.getExibicao().equals(DominioExibicaoParametroCamposLaudo.T)
					|| key.getExibicao().equals(
							DominioExibicaoParametroCamposLaudo.A)) {
				exibir1 = true;
			}
			mascara.setExibeTela(exibir1);

			mascara.setFonte(key.getFonte());
			mascara.setItalico(key.getItalico());
			mascara.setLarguraObjetoVisual(key.getLarguraObjetoVisual());
			mascara.setNegrito(key.getNegrito());
			mascara.setPosicaoColunaImpressao(key.getPosicaoColunaImpressao());
			mascara.setPosicaoLinhaImpressao(key.getPosicaoLinhaImpressao());
			mascara.setQuantidadeCaracteres(key.getQuantidadeCaracteres());
			mascara.setRiscado(key.getRiscado());
			mascara.setSublinhado(key.getSublinhado());
			mascara.setTamanhoFonte(key.getTamanhoFonte().intValue());
			mascara.setValor(entry.getValue());

			mascaras.add(mascara);
		}

		exame.setMascaras(mascaras);

		return exame;

	}

	public Map<AelParametroCamposLaudo, IAelResultadoExame> obterMapaResultadosFicticiosExames(
			List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia) {
		Map<AelParametroCamposLaudo, IAelResultadoExame> resultados = new HashMap<AelParametroCamposLaudo, IAelResultadoExame>();

		for (AelParametroCamposLaudo paramCamLaudo : camposDaVersaoLaudoPrevia) {

			AelResultadoExame resultado = new AelResultadoExame();

			switch (paramCamLaudo.getObjetoVisual()) {
			case TEXTO_ALFANUMERICO:

				resultado.setDescricao("teste prévia ");
				resultados.put(paramCamLaudo, resultado);

				break;

			case TEXTO_NUMERICO_EXPRESSAO:

				resultado.setValor(123L);
				resultados.put(paramCamLaudo, resultado);
				break;

			case TEXTO_LONGO:

				resultado.setDescricao("teste prévia");
				resultados.put(paramCamLaudo, resultado);
				break;

			case TEXTO_CODIFICADO:

				if (paramCamLaudo.getCampoLaudo().getGrupoResultadoCodificado() != null) {
					List<AelResultadoCodificado> resultadosCodificados = this
							.getAelResultadoCodificadoDAO()
							.pesquisarResultadosCodificadosPorCampoLaudo(
									paramCamLaudo.getCampoLaudo());
					if (!resultadosCodificados.isEmpty()) {
						resultado.setResultadoCodificado(resultadosCodificados
								.get(resultadosCodificados.size() / 2));
						resultados.put(paramCamLaudo, resultado);
					}
				} else {
					List<AelExameGrupoCaracteristica> exameGrupoCaracteristicaList = this
							.getAelExameGrupoCaracteristicaDAO()
							.pesquisarExameGrupoCarateristicaPorCampo(
									paramCamLaudo);
					if (!exameGrupoCaracteristicaList.isEmpty()) {
						resultado
								.setResultadoCaracteristica(exameGrupoCaracteristicaList
										.get(exameGrupoCaracteristicaList
												.size() / 2)
										.getResultadoCaracteristica());
						resultados.put(paramCamLaudo, resultado);
					}
				}
				break;

			default:

				break;
			}
		}
		return resultados;
	}

	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private String extraiResultadoString(
			Entry<AelParametroCamposLaudo, IAelResultadoExame> entry)
			throws BaseException {

		AelParametroCamposLaudo parametro = entry.getKey();
		IAelResultadoExame resultado = (IAelResultadoExame) entry.getValue();

		switch (parametro.getObjetoVisual()) {
		case TEXTO_FIXO:
			String textoLivre = parametro.getTextoLivre();
			if (textoLivre != null) {
				textoLivre = CoreUtil.converterRTF2Text(textoLivre.trim());
			}
			return textoLivre;

		case TEXTO_ALFANUMERICO:
			String textoAlfanumerico = null;
			if (resultado != null) {
				if (resultado.getDescricao() != null) {
					textoAlfanumerico = resultado.getDescricao();
				}
			}

			return textoAlfanumerico;

		case TEXTO_NUMERICO_EXPRESSAO:
			String textoNumericoExpressao = null;
			if (resultado != null) {
				Short decimais = parametro.getQuantidadeCasasDecimais() == null ? (short) 0
						: parametro.getQuantidadeCasasDecimais();

				String valor = resultado.getValor() == null ? null : resultado
						.getValor().toString();
				if (valor != null && decimais.intValue() != 0) {
					// exemplo: valor 35 decimais 2 = 0,35
					String aux = StringUtils.leftPad(valor, decimais + 1, "0");
					StringBuffer v = new StringBuffer(aux);
					v.insert(v.length() - decimais, ",");
					textoNumericoExpressao = v.toString();
				} else {
					textoNumericoExpressao = valor;
				}
			}

			return textoNumericoExpressao;

		case TEXTO_LONGO:
			if (resultado == null || resultado.getDescricao() == null) {
				return null;
			}
			return resultado.getDescricao();

		case TEXTO_CODIFICADO:
			String result = null;
			if (resultado != null) {
				if (parametro.getCampoLaudo().getGrupoResultadoCodificado() != null) {
					result = resultado.getResultadoCodificado() == null ? null
							: resultado.getResultadoCodificado().getDescricao();
				} else {
					result = resultado.getResultadoCaracteristica() == null ? null
							: resultado.getResultadoCaracteristica()
									.getDescricao();
				}
			}

			return result;

		case EQUIPAMENTO:
			return "Informações equipamento";

		case METODO:
			return "Informações método";

		case RECEBIMENTO:
			return "Informações recebimento";

		case HISTORICO:
			// TODO avaliar como deve enviar já que são vários textos na lista
			List<String> historico = new ArrayList<String>();
			historico.add("Informação historico 1");
			historico.add("Informação historico 2");

			return historico.toString();

		case VALORES_REFERENCIA:
			String valoresReferencia = "0 a 0";

			return valoresReferencia;

		}

		return null;
	}

	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return new AelResultadoCodificadoDAO();
	}

	protected AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return new AelExameGrupoCaracteristicaDAO();
	}

}
