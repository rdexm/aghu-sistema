package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosExamesHist;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;

/**
 * @HIST MascaraExamesHistON
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.SuspiciousConstantFieldName"})
@Stateless
public class MascaraExamesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MascaraExamesON.class);

	private static final long serialVersionUID = 2831485648403262351L;

	public static int UNIQUE = 0;
	
	@EJB
	private IAghuFacade iAghuFacade;

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}

	@Inject
	private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;

	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;

	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;

	@Inject
	private AelCampoVinculadoDAO aelCampoVinculadoDAO;

	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;

	@Inject
	private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;

	/**
	 * @HIST MascaraExamesHistON.obterMapaResultadosExamesHist
	 * @param itemSolicitacaoExame
	 * @return
	 */
	public Map<AelParametroCampoLaudoId, Object> obterMapaResultadosExames(Object itemSolicitacaoExameObject) {
		Map<AelParametroCampoLaudoId, Object> resultados = new HashMap<AelParametroCampoLaudoId, Object>();

		if (itemSolicitacaoExameObject != null && itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {// Então NÃO HISTÓRICO
			List<AelResultadoExame> resultadosDoExame = this.getAelResultadoExameDAO().listarResultadosExame((AelItemSolicitacaoExames) itemSolicitacaoExameObject);
			
			for (AelResultadoExame resultado : resultadosDoExame) {
//				if (resultado != null && (resultado.getResultadoCodificado() == null && resultado.getDescricaoResultado() == null)) {
//					resultado.setDescricaoResultado(getAelDescricoesResultadoDAO().obterDescricaoResultadoPorResultado(resultado));
//				}
				resultados.put(resultado.getParametroCampoLaudo().getId(), resultado);
			}
		} else {
			if (itemSolicitacaoExameObject != null && itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				List<AelResultadosExamesHist> resultadosDoExame = this.getAelResultadoExameDAO().listarResultadosExameHist(
						(AelItemSolicExameHist) itemSolicitacaoExameObject);
				for (AelResultadosExamesHist resultado : resultadosDoExame) {
					resultados.put(resultado.getParametroCampoLaudo().getId(), resultado);
				}
			}
		}

		return resultados;
	}

	public List<AelParametroCamposLaudo> obterListaCamposExibicao(Object itemSolicitacaoExameObject,
			Map<AelParametroCamposLaudo, Object> resultados, List<AelParametroCamposLaudo> camposDaVersao, Boolean isHist) {
		List<AelParametroCamposLaudo> camposDaVersaoRetorno = new ArrayList<AelParametroCamposLaudo>();

		if (camposDaVersao != null) {

			for (AelParametroCamposLaudo aelParametroCampoLaudo : camposDaVersao) {

				/* Pega todos os campos que vincularam o campo */
				List<AelCampoVinculado> camposVinculadores = getAelCampoVinculadoDAO().pesquisarCampoVinculadoresPorParametroCampoLaudo(
						aelParametroCampoLaudo);

				boolean results = false;
				if (isHist) {
					AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
					List<AelResultadosExamesHist> lista = getAelResultadoExameDAO().listarResultadosExamesHist(item.getId().getSoeSeq(),
							item.getId().getSeqp(), aelParametroCampoLaudo.getId().getVelEmaExaSigla(),
							aelParametroCampoLaudo.getId().getVelEmaManSeq(), aelParametroCampoLaudo.getId().getVelSeqp(),
							aelParametroCampoLaudo.getId().getCalSeq(), aelParametroCampoLaudo.getId().getSeqp());
					results = !lista.isEmpty();
				} else {
					AelItemSolicitacaoExames item = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
					List<AelResultadoExame> lista = getAelResultadoExameDAO().listarResultadosExames(item.getId().getSoeSeq(),
							item.getId().getSeqp(), aelParametroCampoLaudo.getId().getVelEmaExaSigla(),
							aelParametroCampoLaudo.getId().getVelEmaManSeq(), aelParametroCampoLaudo.getId().getVelSeqp(),
							aelParametroCampoLaudo.getId().getCalSeq(), aelParametroCampoLaudo.getId().getSeqp());
					results = !lista.isEmpty();
				}

				if (camposVinculadores != null && !camposVinculadores.isEmpty() && !results) {
					boolean algumCampoVincula = false;
					/*
					 * Verifica se os campos que vincularam o campo tem
					 * resultados buscando map de resultados
					 */
					for (AelCampoVinculado campoVinculado : camposVinculadores) {
						// Pega do map
						algumCampoVincula = this.processarResultadoEDescricaoExame(resultados, campoVinculado, isHist);

						if (algumCampoVincula) {
							camposDaVersaoRetorno.add(aelParametroCampoLaudo);
							break;
						}
					}
				} else {
					camposDaVersaoRetorno.add(aelParametroCampoLaudo);
				}
			}
		}

		return camposDaVersaoRetorno;
	}

	private boolean processarResultadoEDescricaoExame(Map<AelParametroCamposLaudo, Object> resultados, AelCampoVinculado campoVinculado,
			Boolean isHist) {

		if (isHist) {
			AelResultadosExamesHist resultado = (AelResultadosExamesHist) resultados.get(campoVinculado
					.getAelParametroCamposLaudoByAelCvcPclFk1());

			if (resultado != null
					&& (resultado.getValor() != null || resultado.getDescricao() != null
							|| resultado.getResultadoCodificado() != null || resultado.getResultadoCaracteristica() != null)) {
				return Boolean.TRUE;
			}
		} else {
			AelResultadoExame resultado = (AelResultadoExame) resultados.get(campoVinculado.getAelParametroCamposLaudoByAelCvcPclFk1());

			if (resultado != null
					&& (resultado.getValor() != null || resultado.getDescricao() != null
							|| resultado.getResultadoCodificado() != null || resultado.getResultadoCaracteristica() != null)) {

				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public Map<AelParametroCampoLaudoId, AelResultadoExame> obterMapaResultadosPadraoExames(AelResultadosPadrao resultPadrao) {
		
		List<AelResultadoPadraoCampo> resultadosDoExame = this.getAelResultadoPadraoCampoDAO().pesquisarResultadoPadraoCampoPorResultadoPadraoSeq(resultPadrao.getSeq());
		
		Map<AelParametroCampoLaudoId, AelResultadoExame> resultados = new HashMap<AelParametroCampoLaudoId, AelResultadoExame>();

		for (AelResultadoPadraoCampo resultado : resultadosDoExame) {

			AelResultadoExame resultadoNormal = new AelResultadoExame();

			if (resultado != null && (resultado.getResultadoCodificado() == null)) {

				AelDescricoesResulPadrao descricaoResultPadrao = getAelDescricoesResulPadraoDAO().obterAelDescricoesResulPadraoPorID(
						resultado.getId().getRpaSeq(), resultado.getId().getSeqp());

				if (descricaoResultPadrao != null) {
					resultadoNormal.setDescricao(descricaoResultPadrao.getDescricao());
				}
			} else {
				resultadoNormal.setResultadoCodificado(resultado.getResultadoCodificado());
				resultadoNormal.setResultadoCaracteristica(resultado.getResultadoCaracteristica());
				resultadoNormal.setParametroCampoLaudo(resultado.getParametroCampoLaudo());
			}

			if (resultado.getValor() != null) {
				resultadoNormal.setValor(resultado.getValor());
			}

			resultados.put(resultado.getParametroCampoLaudo().getId(), resultadoNormal);
		}

		return resultados;
	}

	public Map<AelParametroCamposLaudo, AelResultadoExame> obterMapaResultadosFicticiosExames(
			List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia) {
		Map<AelParametroCamposLaudo, AelResultadoExame> resultados = new HashMap<AelParametroCamposLaudo, AelResultadoExame>();

		for (AelParametroCamposLaudo paramCamLaudo : camposDaVersaoLaudoPrevia) {

			AelResultadoExame resultado = new AelResultadoExame();

			switch (paramCamLaudo.getObjetoVisual()) {
			case TEXTO_ALFANUMERICO:
				resultado.setDescricao("teste prévia " + new Date().getTime());
				resultados.put(paramCamLaudo, resultado);

				break;

			case TEXTO_NUMERICO_EXPRESSAO:
				resultado.setValor(12345L);
				resultados.put(paramCamLaudo, resultado);
				break;

			case TEXTO_LONGO:
				resultado.setDescricao("teste prévia" + new Date().getTime());
				resultados.put(paramCamLaudo, resultado);
				break;

			case TEXTO_CODIFICADO:

				if (paramCamLaudo.getCampoLaudo().getGrupoResultadoCodificado() != null) {
					List<AelResultadoCodificado> resultadosCodificados = this.getAelResultadoCodificadoDAO()
							.pesquisarResultadosCodificadosPorCampoLaudo(paramCamLaudo.getCampoLaudo());
					if (!resultadosCodificados.isEmpty()) {
						resultado.setResultadoCodificado(resultadosCodificados.get(resultadosCodificados.size() / 2));
						resultados.put(paramCamLaudo, resultado);
					}
				} else {
					List<AelExameGrupoCaracteristica> exameGrupoCaracteristicaList = this.getAelExameGrupoCaracteristicaDAO()
							.pesquisarExameGrupoCarateristicaPorCampo(paramCamLaudo);
					if (!exameGrupoCaracteristicaList.isEmpty()) {
						resultado.setResultadoCaracteristica(exameGrupoCaracteristicaList.get(exameGrupoCaracteristicaList.size() / 2)
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

	public void processarDescricaoConvenioPaciente(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject) {
		FatConvenioSaude convenioSaude = null;
		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolic = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			convenioSaude = itemSolic.getSolicitacaoExame().getConvenioSaude();
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist itemSolic = aelItemSolicExameHistDAO
						.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
				convenioSaude = itemSolic.getSolicitacaoExame().getConvenioSaude();
			}
		}
		if (convenioSaude != null) {
			cabecalhoLaudo.setConvenioPaciente(convenioSaude.getDescricao());
		}

	}

	public String buscaNomeMedicoSolicitante(Object itemSolicitacaoExameObject) {
		AghAtendimentos atendimento = null;
		RapServidores servidorResponsabilidade = null;
		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolic = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			atendimento = itemSolic.getSolicitacaoExame().getAtendimento();
			servidorResponsabilidade = itemSolic.getSolicitacaoExame().getServidorResponsabilidade();
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist itemSolic = aelItemSolicExameHistDAO
						.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
				atendimento = itemSolic.getSolicitacaoExame().getAtendimento();
				servidorResponsabilidade = itemSolic.getSolicitacaoExame().getServidorResponsabilidade();
			}
		}

		if (atendimento != null && atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno().getMedicoExterno() != null) {
			return atendimento.getAtendimentoPacienteExterno().getMedicoExterno().getNome();

		} else if (servidorResponsabilidade != null && servidorResponsabilidade.getPessoaFisica() != null) {
			return servidorResponsabilidade.getPessoaFisica().getNome();
		}

		return "";
	}

	/*
	 * public void buscarMaiorDataLiberacao(AelpCabecalhoLaudoVO cabecalhoLaudo,
	 * Object itemSolicitacaoExameObject) { Integer seqSolicitacaoExame = null;
	 * Short unfSeq = null;
	 * 
	 * if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
	 * AelItemSolicitacaoExames itemSolic = aelItemSolicitacaoExameDAO
	 * .obterPorChavePrimaria(((AelItemSolicitacaoExames)
	 * itemSolicitacaoExameObject).getId()); seqSolicitacaoExame =
	 * itemSolic.getSolicitacaoExame().getSeq(); unfSeq =
	 * itemSolic.getUnidadeFuncional().getSeq();
	 * cabecalhoLaudo.setDataLiberacao(
	 * getExamesLaudosFacade().buscaMaiorDataLiberacao(seqSolicitacaoExame,
	 * unfSeq)); } else { if (itemSolicitacaoExameObject instanceof
	 * AelItemSolicExameHist) { AelItemSolicExameHist itemSolic =
	 * aelItemSolicExameHistDAO .obterPorChavePrimaria(((AelItemSolicExameHist)
	 * itemSolicitacaoExameObject).getId()); seqSolicitacaoExame =
	 * itemSolic.getSolicitacaoExame().getSeq(); unfSeq =
	 * itemSolic.getUnidadeFuncional().getSeq();
	 * cabecalhoLaudo.setDataLiberacao(
	 * getExamesLaudosFacade().buscaMaiorDataLiberacaoHist(seqSolicitacaoExame,
	 * unfSeq)); } }
	 * 
	 * }
	 */

	public void verificaSeMostraSeloDeAcreditacao(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject) {
		AghUnidadesFuncionais unidadeFuncional = null;

		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolic = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			unidadeFuncional = itemSolic.getSolicitacaoExame().getUnidadeFuncional();

		} else if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
			AelItemSolicExameHist itemSolic = aelItemSolicExameHistDAO
					.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
			unidadeFuncional = itemSolic.getSolicitacaoExame().getUnidadeFuncional();
		}
		
		boolean possuiCaracteristica = iAghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.USA_SELO_LAUDO);
		cabecalhoLaudo.setIndMostrarSeloAcreditacao(possuiCaracteristica);
	}

	public String montaNomeExameCabecalho(final Object itemSolicitacaoExameObject, boolean isPrevia) {
		AelExames exame = null;
		AelMateriaisAnalises materialAnalise = null;
		String descMaterialAnalise = null;
		AelRegiaoAnatomica regiaoAnatomica = null;
		String descRegiaoAnatomica = null;

		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames item = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			exame = item.getExame();
			materialAnalise = item.getMaterialAnalise();
			descMaterialAnalise = item.getDescMaterialAnalise();
			regiaoAnatomica = item.getRegiaoAnatomica();
			descRegiaoAnatomica = item.getDescRegiaoAnatomica();
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
				exame = item.getExame();
				materialAnalise = item.getMaterialAnalise();
				descMaterialAnalise = item.getDescMaterialAnalise();
				regiaoAnatomica = item.getRegiaoAnatomica();
				descRegiaoAnatomica = item.getDescRegiaoAnatomica();
			}
		}
		return montaNomeExameCabecalho(exame, materialAnalise, descMaterialAnalise, regiaoAnatomica, descRegiaoAnatomica, isPrevia);
	}

	private String montaNomeExameCabecalho(AelExames exame, AelMateriaisAnalises materialAnalise, String descMaterialAnalise,
			AelRegiaoAnatomica regiaoAnatomica, String descRegiaoAnatomica, boolean isPrevia) {
		String materialAnaliseString = null;
		String regiaoAnatomicaString = null;

			StringBuffer nomeExame = new StringBuffer();
			if (isPrevia) {
				nomeExame.append("Nome Exame Teste(Sangue)");
			} else {
				nomeExame.append(exame.getDescricao());
				// Se coletável
				if (materialAnalise.getIndColetavel()) {
					if (!materialAnalise.getIndExigeDescMatAnls()) {
						if (materialAnalise.getDescricao() != null && !materialAnalise.getDescricao().isEmpty()) {
							materialAnaliseString = "(" + materialAnalise.getDescricao();
							nomeExame.append(materialAnaliseString);
						}
					} else {
						if (descMaterialAnalise != null && !descMaterialAnalise.isEmpty()) {
							materialAnaliseString = "(" + descMaterialAnalise;
							nomeExame.append(materialAnaliseString);
						}
					}
				}

				if (regiaoAnatomica != null) {
					if (materialAnaliseString == null) {
						regiaoAnatomicaString = "(" + regiaoAnatomica.getDescricao();
					} else {
						regiaoAnatomicaString = " - " + regiaoAnatomica.getDescricao();
						nomeExame.append(regiaoAnatomicaString);
					}
				}
				if (descRegiaoAnatomica != null) {
					if (materialAnaliseString == null && regiaoAnatomicaString == null) {
						regiaoAnatomicaString = "(" + descRegiaoAnatomica;
					} else {
						regiaoAnatomicaString = " - " + descRegiaoAnatomica;
						nomeExame.append(regiaoAnatomicaString);
					}
				}
				if (materialAnaliseString != null || regiaoAnatomicaString != null) {
					nomeExame.append(')');
				}
			}
			return nomeExame.toString();
		}

//	private String montaNomeExamePatologia(AelExames exame, NumeroApTipoVO numeroApTipoVO) {
//		String nomeExame = this.aelItemSolicitacaoExameDAO.obterTipoExamePatologico(numeroApTipoVO.getNumeroAp(),
//				numeroApTipoVO.getLu2Seq());
//		return nomeExame;
//	}

	public String recuperaLeito(final Object itemSolicitacaoExameObject) {
		String leito = null;
		AghUnidadesFuncionais unidadeFuncional = null;
		AghAtendimentos atendimento = null;

		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolic = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			unidadeFuncional = itemSolic.getSolicitacaoExame().getUnidadeFuncional();
			atendimento = itemSolic.getSolicitacaoExame().getAtendimento();
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist itemSolic = aelItemSolicExameHistDAO
						.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
				unidadeFuncional = itemSolic.getSolicitacaoExame().getUnidadeFuncional();
				atendimento = itemSolic.getSolicitacaoExame().getAtendimento();
			}
		}
		
		boolean isUnidadeEmergencia = iAghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);

		if (isUnidadeEmergencia) {
			leito = unidadeFuncional.getDescricao();
		} else {
			if (atendimento != null && atendimento.getLeito() != null) {
				leito = "Leito:" + atendimento.getLeito().getLeitoID();
			}
		}

		return leito;
	}

	/**
	 * @HIST MascaraExamesHistON.obterSexoPacienteHist
	 * @param itemSolicitacaoExame
	 * @return
	 */
	public String obterSexoPaciente(Object itemSolicitacaoExameObject) {
		String retorno = "";
		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolicitacaoExame = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			if (itemSolicitacaoExame != null && itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null
					&& itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente() != null) {
				retorno = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getSexo().toString();
			}
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist itemSolicitacaoExame = aelItemSolicExameHistDAO
						.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
				if (itemSolicitacaoExame != null && itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null
						&& itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente() != null) {
					retorno = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getSexo().toString();
				}
			}
		}
		return retorno;

	}

	/**
	 * @HIST MascaraExamesHistON.obterIdadePacienteHist
	 * @param itemSolicitacaoExame
	 * @return
	 */
	public Integer obterIdadePaciente(Object itemSolicitacaoExameObject) {
		Integer retorno = 0;
		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames itemSolicitacaoExame = aelItemSolicitacaoExameDAO
					.obterPorChavePrimaria(((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId());
			if (itemSolicitacaoExame != null && itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null
					&& itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente() != null) {
				retorno = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getIdade();
			}
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist itemSolicitacaoExame = aelItemSolicExameHistDAO
						.obterPorChavePrimaria(((AelItemSolicExameHist) itemSolicitacaoExameObject).getId());
				if (itemSolicitacaoExame != null && itemSolicitacaoExame.getSolicitacaoExame().getAtendimento() != null
						&& itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente() != null) {
					retorno = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getIdade();
				}
			}
		}
		return retorno;
	}	

	/** GET **/
	private AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}

	private AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	private AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

	private AelCampoVinculadoDAO getAelCampoVinculadoDAO() {
		return aelCampoVinculadoDAO;
	}

	private AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}

	private AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return aelDescricoesResulPadraoDAO;
	}

	public IAghuFacade getiAghuFacade() {
		return iAghuFacade;
	}
}