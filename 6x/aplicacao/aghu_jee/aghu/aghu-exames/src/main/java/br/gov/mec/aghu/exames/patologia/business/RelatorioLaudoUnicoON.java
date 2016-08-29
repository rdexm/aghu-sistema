package br.gov.mec.aghu.exames.patologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExameRelLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.RelatorioLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.VRapServidorConselhoVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;

@Stateless
public class RelatorioLaudoUnicoON extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory
			.getLog(RelatorioLaudoUnicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelExtratoExameApDAO aelExtratoExameApDAO;

	@Inject
	private AelPatologistaDAO aelPatologistaDAO;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = -7773076693923845366L;

	protected void obtemDataAssinatura(AelExameAp exame,
			RelatorioLaudoUnicoVO vo) {

		List<AelExtratoExameAp> extratos = aelExtratoExameApDAO.listarAelExtratoExameApPorLuxSeq(vo.getLuxSeq());
		if (extratos != null && !extratos.isEmpty()) {
			if (!DominioSituacaoExamePatologia.LA.equals(extratos.get(0)
					.getEtapasLaudo())) {
				return;
			}
			for (AelExtratoExameAp extrato : extratos) {
				if (DominioSituacaoExamePatologia.LA.equals(extrato
						.getEtapasLaudo())) {
					vo.setDtAssinatura(extrato.getCriadoEm());
					break;
				}
			}
		}
	}

	protected void obtemNotasAdicionais(RelatorioLaudoUnicoVO vo) {
		final List<AelNotaAdicionalAp> notas = examesPatologiaFacade
				.obterListaNotasAdicionaisPeloExameApSeq(vo.getLuxSeq());

		final StringBuffer notasAdicionais = new StringBuffer(20);
		Boolean possuiNotas = false;
		for (AelNotaAdicionalAp nota : notas) {
			possuiNotas = true;
			notasAdicionais.append("Em: ").append(
					DateUtil.dataToString(nota.getCriadoEm(),
							"dd/MM/yyyy hh:mm"));
			final AelPatologista patologista = aelPatologistaDAO
					.obterPatologistaAtivoPorServidor(nota.getRapServidores());
			if (patologista == null) {
				notasAdicionais
						.append(" Por: ")
						.append(nota.getRapServidores().getPessoaFisica() != null ? nota
								.getRapServidores().getPessoaFisica().getNome()
								: "");
			} else {
				VRapServidorConselho conselho = registroColaboradorFacade
						.obterVRapServidorConselhoPeloId(
								patologista.getServidor().getId()
										.getMatricula(),
								patologista.getServidor().getId()
										.getVinCodigo(), null);
				notasAdicionais.append(" Por: ")
						.append(patologista.getNomeParaLaudo()).append(_HIFEN_)
						.append(patologista.getFuncao().getDescricao())
						.append(_HIFEN_).append("CRM").append(' ')
						.append(conselho.getNroRegConselho());
			}
			notasAdicionais.append("<br/>").append(nota.getNotas())
					.append("<br/><br/>");
		}
		if (possuiNotas) {
			vo.setNotasAdicionais(notasAdicionais.toString());
		}
	}
	
	protected void obtemCrm(RelatorioLaudoUnicoVO vo) {
		
		if(vo.getvCrm1() != null){
			vo.setColResp1(generateColResp(vo.getvCrm1(), vo.getvPatologista1(), vo.getvFuncao1()));
		}

		if(vo.getvCrm2() != null){
			vo.setColResp2(generateColResp(vo.getvCrm2(), vo.getvPatologista2(), vo.getvFuncao2()));
		}

		if(vo.getvCrm3() != null){
			vo.setColResp3(generateColResp(vo.getvCrm3(), vo.getvPatologista3(), vo.getvFuncao3()));
		}
	}

	public List<RelatorioLaudoUnicoVO> obterRelatorioLaudoUnicoVO (
			final String nroAps, final AelConfigExLaudoUnico aelConfigExLaudoUnico) throws BaseException {
		final List<RelatorioLaudoUnicoVO> vos = new ArrayList<RelatorioLaudoUnicoVO>();
		final String SEPARADOR_VIRGUAL = ",";

		String nroAp = nroAps.split(SEPARADOR_VIRGUAL)[0];

		final List<VAelApXPatologiaAghu> vAghus = examesPatologiaFacade
				.pesquisarVAelApXPatologiaAghu(0, 10, null, true, null, null,
						null, null, null, aelConfigExLaudoUnico, Long.valueOf(nroAp));

		final VAelApXPatologiaAghu vAghu = vAghus.get(0);
		final RelatorioLaudoUnicoVO vo = new RelatorioLaudoUnicoVO(vAghu);

		final String projeto = examesFacade.aelcGetProjAtend(vo.getAtdSeq(),
				vo.getAtvSeq());
		if (projeto != null && projeto.length() > 43) {
			vo.setProjeto(projeto.substring(0, 43));
		} else {
			vo.setProjeto(projeto);
		}

		vo.setMedicoSolicitante(aelcBuscaMedSolic(vo));

		final AelItemSolicitacaoExameRelLaudoUnicoVO aiserluVo = examesFacade
				.obterAelItemSolicitacaoExameRelLaudoUnicoVOPorLuxSeq(
						vo.getLuxSeq());

		if (aiserluVo != null) {
			vo.createCabecalho(aelpCabecalhoLaudo());
			vo.setLeito(cfLeito(vo, aiserluVo));
		}

		final AelExameAp aelExameAp = examesPatologiaFacade
				.obterAelExameApPorChavePrimaria(vo.getLuxSeq());
		if (aelExameAp != null) {
			if (aelExameAp.getAelAnatomoPatologicoOrigem() != null) {
				vo.setLumNumeroApOrigem(aelExameAp.getAelAnatomoPatologicoOrigem().getNumeroAp());
			} else {
				vo.setLumNumeroApOrigem(null);
			}
			obtemDataAssinatura(aelExameAp, vo);
		}

		vo.setProntuario(aelcBuscaProntPac(vo.getAtdSeq(), vo.getAtvSeq()));

		vo.setDtExtrato(aelcBuscaDtExtr(vo.getLuxSeq(),
				DominioSituacaoExamePatologia.RE));

		cfNumeracao(vAghu, vo);

		final AelMacroscopiaAps macroscopia = examesPatologiaFacade
				.obterAelMacroscopiaApsPorAelExameAps(vAghu.getId().getLuxSeq());
		if (macroscopia != null) {
			vo.setMacroscopia(macroscopia.getMacroscopia());
		}

		final AelDiagnosticoAps diagnostico = examesPatologiaFacade
				.obterAelDiagnosticoApsPorAelExameAps(vAghu.getId().getLuxSeq());
		if (diagnostico != null) {
			vo.setDiagnostico(diagnostico.getDiagnostico());
		}

		final AelInformacaoClinicaAP info = aelInformacaoClinicaApDAO
				.obterAelInformacaoClinicaApPorAelExameAps(aelExameAp.getSeq());
		if (info != null) {
			vo.setInformacoesClinicas(info.getInformacaoClinica());
		}

		aelpGetPatolLaudo(vo);
		obtemCrm(vo);
		obtemNotasAdicionais(vo);

		vos.add(vo);

		return vos;
	}

	private void cfNumeracao(final VAelApXPatologiaAghu vAghu,
			final RelatorioLaudoUnicoVO vo) {
		final List<AelMaterialAp> materiais = examesPatologiaFacade
				.obterAelMaterialApPorAelExameAps(vo.getLuxSeq());

		final StringBuffer material = new StringBuffer();
		for (AelMaterialAp aelMaterialAp : materiais) {
			material.append(aelMaterialAp.getOrdem()).append(") ")
					.append(aelMaterialAp.getMaterial()).append("\n\n");
		}

		if (material.length() > 0) {
			material.delete((material.length() - 2), material.length());
		}
		vo.setLuxMateriais(material.toString());
	}

	private String cfLeito(final RelatorioLaudoUnicoVO vo,
			final AelItemSolicitacaoExameRelLaudoUnicoVO aiserluVo) {
		// Verifica se a Unidade Funcional da Solicitação é de Emergência

		final AelSolicitacaoExames soe = aiserluVo.getSolicitacaoExame();
		final boolean isUnidadeEmergencia = aghuFacade
				.validarCaracteristicaDaUnidadeFuncional(
						aiserluVo.getUnidadeFuncional().getSeq(),
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		final AghUnidadesFuncionais unf = aiserluVo.getUnidadeFuncional();
		if (unf != null) {
			vo.setUnidadeFuncional(unf.getDescricao());
		}

		// Pac da Emergência
		if (isUnidadeEmergencia) {
			return "Emergência";
		}

		final AghAtendimentos atendimento = soe.getAtendimento();

		if (atendimento != null) {
			final AinLeitos leito = atendimento.getLeito();
			if (leito != null) {
				return "Leito: " + leito.getLeito();
			}
		}

		return null;
	}

	private String generateColResp(final String vCrm,
			final String vPatologista, final String vFuncao) {
		final StringBuffer sb = new StringBuffer();

		if (vPatologista.length() > 30) {
			sb.append(' ').append(vPatologista.substring(0, 30));

		} else {
			sb.append(' ').append(vPatologista);
		}

		sb.append('\n');

		if (vFuncao.length() > 11) {
			sb.append(' ').append(vFuncao.substring(0, 11));

		} else {
			sb.append(' ').append(vFuncao);
		}

		sb.append(" CRM ");
		if (vCrm.length() > 8) {
			sb.append(vCrm.substring(0, 8));
		} else {
			sb.append(vCrm);
		}
		return sb.toString();
	}

	/**
	 * ORADB: AELC_BUSCA_MED_SOLIC função que retorna o Médico Solicitante,
	 * chamada no forms aelf_lista_laud_unic e no report aelr_laudo_unico
	 */
	String aelcBuscaMedSolic(final RelatorioLaudoUnicoVO relatorioVo) {

		if (relatorioVo.getAtdSeq() != null) {
			final AghAtendimentos aghAtendimento = aghuFacade
					.obterAghAtendimentoPorChavePrimaria(
							relatorioVo.getAtdSeq());

			// verifica se é um médico externo
			if (aghAtendimento != null) {
				AghAtendimentosPacExtern aape = aghAtendimento
						.getAtendimentoPacienteExterno();
				if (aape != null) {
					final AghMedicoExterno medicoExterno = aape
							.getMedicoExterno();
					if (medicoExterno != null) {
						return medicoExterno.getNome();
					}
				}
			}
		}

		String medico = registroColaboradorFacade
				.obterNomePessoaServidorPorAelExameApItemSolics(
						relatorioVo.getLuxSeq());
		if (medico != null) {
			return medico;
		}

		return "";

	}

	/**
	 * ORADB: AELP_GET_PATOL_LAUDO
	 * 
	 * Procedure que retorna os tres primeiros patologistas respeitando a
	 * ordem_medico_laudo. Usada no report aelr_laudo_unico
	 * 
	 * @param luxSeq
	 * @param etapasLaudo
	 */
	void aelpGetPatolLaudo(final RelatorioLaudoUnicoVO relatorioVo) {

		List<VRapServidorConselhoVO> vos = registroColaboradorFacade
				.obterVRapServidorConselhoVO(relatorioVo.getLuxSeq());

		int vCont = 0;
		for (VRapServidorConselhoVO vo : vos) {
			vCont++;

			if (vCont > 3) {
				break;

			} else if (vo == null) {
				continue;
			}

			switch (vCont) {
			case 1:
				relatorioVo.setvCrm1(vo.getVcsNroRegConselho());
				relatorioVo.setvFuncao1(vo.getLuiFuncao().getDescricao());
				relatorioVo.setvPatologista1(vo.getLuiNomeParaLaudo());
				break;

			case 2:
				relatorioVo.setvCrm2(vo.getVcsNroRegConselho());
				relatorioVo.setvFuncao2(vo.getLuiFuncao().getDescricao());
				relatorioVo.setvPatologista2(vo.getLuiNomeParaLaudo());
				break;

			case 3:
				relatorioVo.setvCrm3(vo.getVcsNroRegConselho());
				relatorioVo.setvFuncao3(vo.getLuiFuncao().getDescricao());
				relatorioVo.setvPatologista3(vo.getLuiNomeParaLaudo());
				break;
			}
		}
	}

	/**
	 * ORADB: AELC_BUSCA_DT_EXTR
	 * 
	 * @param luxSeq
	 * @param etapasLaudo
	 */
	Date aelcBuscaDtExtr(final Long luxSeq,
			final DominioSituacaoExamePatologia etapasLaudo) {
		final Date d = examesPatologiaFacade
				.obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, etapasLaudo);
		return d;
	}

	/**
	 * ORADB: AELC_BUSCA_PRONT_PAC
	 */
	Integer aelcBuscaProntPac(final Integer atdSeq, final Integer atvSeq) {
		if (atdSeq != null) {
			// função chamada no report aelr_laudo_unico
			final AghAtendimentos atendimento = aghuFacade
					.obterAghAtendimentoPorChavePrimaria(atdSeq);
			if (atendimento != null && atendimento.getPaciente() != null) {
				return atendimento.getPaciente().getProntuario();
			}

		} else if (atvSeq != null) {
			final AelAtendimentoDiversos atendimentoDiversos = examesFacade
					.obterAelAtendimentoDiversosPorChavePrimaria(atvSeq);

			if (atendimentoDiversos != null) {
				return atendimentoDiversos.getAipPaciente().getProntuario();
			}
		}

		return null;
	}

	/**
	 * ORADB: AELP_CABECALHO_LAUDO
	 * 
	 * 
	 * Valores Retornados pela Procedure aonde a unidade executora for anatomia
	 */
	public AelpCabecalhoLaudoVO aelpCabecalhoLaudo()
			throws ApplicationBusinessException {
		final Short unfSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNID_ANATOMIA)
				.getVlrNumerico().shortValue();
		return obterAelpCabecalhoLaudo(unfSeq);
	}

	/**
	 * ORADB: AELP_CABECALHO_LAUDO
	 * 
	 * 
	 * Valores Retornados pela Procedure: -- p_ind_imprime_novo-> se a unidade
	 * possui a caracteristica "Usa Novo Laudo" retona 'S' SENÃO retorna 'N'
	 * Campo utilizado pelo delphi durante o período de transição, para decidir
	 * se imprime o lay-out novo do relatório (S) ou o antigo (N) ----- Dados do
	 * Serviço-> p_desc_servico p_registro p_chefe_servico p_conselho
	 * p_nro_conselho
	 * 
	 * Os dados do serviço são buscados da tabela cct(centro de custos): Se a
	 * unidade pai da unidade é o último nível da hierarquia (não possui pai)
	 * busca as informações do cct da unidade(cct_codigo) SENÃO busca do cct pai
	 * (cct_superior) do cct da unidade.
	 * 
	 * Este teste é feito devido aos casos de unidades que NÃO estão ligadas a
	 * uma área executora de exames -- Dados da Unidade->
	 * p_desc_unidade=descrição da unidade p_chefe_unidade p_conselho_unid
	 * p_nro_conselho_unid
	 * 
	 * Se a unidade possui a característica "Imprime Nome Chefia" retorna o
	 * chefe do cct da unidade ou caso o chefe do cct seja nulo retorna o chefe
	 * da própria unidade Se Não possui a caracterísca ou Não encontrou nenhum
	 * dos 2 chefes retorna nulo
	 * 
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.CyclomaticComplexity" })
	public AelpCabecalhoLaudoVO obterAelpCabecalhoLaudo(final Short unfSeq)
			throws ApplicationBusinessException {

		final AelpCabecalhoLaudoVO cabecalho = new AelpCabecalhoLaudoVO();

		// dados da unidade
		final AghUnidadesFuncionais unf = aghuFacade
				.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		if (unf != null) {
			cabecalho.setpIndImprimeNovo(aghuFacade
					.validarCaracteristicaDaUnidadeFuncional(unfSeq,
							ConstanteAghCaractUnidFuncionais.USA_NOVO_LAUDO));
			final boolean indImprimeChefe = aghuFacade
					.validarCaracteristicaDaUnidadeFuncional(
							unfSeq,
							ConstanteAghCaractUnidFuncionais.IMPRIME_NOME_CHEFIA);
			cabecalho.setpDescUnidade(unf.getDescricao());

			final RapServidores chefia;
			// Retorna o chefe do cct, caso esteja nulo retorna o chefe da
			// unidade
			if (unf.getCentroCusto() != null && unf.getCentroCusto().getRapServidor() != null) {
				chefia = unf.getCentroCusto().getRapServidor();
			} else {
				chefia = unf.getRapServidorChefia();
			}

			// Busca dados do chefe da unidade
			if (chefia != null) {
				cabecalho.setpChefeUnidade(ambulatorioFacade
						.obterDescricaoCidCapitalizada(chefia.getPessoaFisica()
								.getNome(), CapitalizeEnum.TODAS));

				final List<RapQualificacao> listaQualificacao = registroColaboradorFacade
						.pesquisarQualificacaoConselhoProfissionalPorServidor(
								chefia.getId().getMatricula(),
								chefia.getId().getVinCodigo());

				if (!listaQualificacao.isEmpty()) {
					final RapQualificacao qualificacao = listaQualificacao
							.get(0);
					final RapConselhosProfissionais rcp = qualificacao
							.getTipoQualificacao().getConselhoProfissional();
					if (rcp != null) {
						cabecalho.setpConselhoUnid(rcp.getSigla());
					}

					cabecalho.setpNroConselhoUnid(qualificacao
							.getNroRegConselho());
				}
			}

			// busca dados unidade pai
			final AghUnidadesFuncionais unidPai = unf.getUnfSeq();

			final FccCentroCustos centroCusto;
			// busca o cct do serviço
			if (unidPai == null || unidPai.getUnfSeq() == null) {
				centroCusto = unf.getCentroCusto();
				if (cabecalho.getpChefeUnidade() == null) {
					cabecalho.setpDescUnidade(null);
				}
			} else {
				centroCusto = unf.getCentroCusto().getCentroCusto();
			}

			// busca os dados do serviço a partir do cct selecionado acima
			if (centroCusto != null) {
				cabecalho.setpDescServico(centroCusto.getDescricao());
				cabecalho.setpRegistro(centroCusto.getRegistroFuncionamento());

				// busca dados do chefe do serviço
				final RapServidores chefeServico = centroCusto.getRapServidor();
				if (chefeServico != null) {
					cabecalho.setpChefeServico(ambulatorioFacade
							.obterDescricaoCidCapitalizada(chefeServico
									.getPessoaFisica().getNome(),
									CapitalizeEnum.TODAS));

					final List<RapQualificacao> listaQualificacao = registroColaboradorFacade
							.pesquisarQualificacaoConselhoProfissionalPorServidor(
									chefeServico.getId().getMatricula(),
									chefeServico.getId().getVinCodigo());

					if (!listaQualificacao.isEmpty()) {
						final RapQualificacao qualificacao = listaQualificacao
								.get(0);
						final RapConselhosProfissionais rcp = qualificacao
								.getTipoQualificacao()
								.getConselhoProfissional();
						if (rcp != null) {
							cabecalho.setpConselho(rcp.getSigla());
						}
						cabecalho.setpNroConselho(qualificacao
								.getNroRegConselho());
					}

				} else {
					cabecalho.setpChefeServico(null);
					cabecalho.setpConselho(null);
					cabecalho.setpNroConselho(null);
				}

				// monta linhas do cabeçalho
				if (cabecalho.getpRegistro() != null) {
					cabecalho.setpLinhaCab1(cabecalho.getpDescServico() + _HIFEN_
							+ cabecalho.getpRegistro());
				} else {
					cabecalho.setpLinhaCab1(cabecalho.getpDescServico());
				}

				String pChefe = "";
				if(cabecalho.getpChefeServico() != null) {
					pChefe = cabecalho.getpChefeServico();
				}
				
				String pConselho = "";
				if(cabecalho.getpConselho() != null) {
					pConselho = cabecalho.getpConselho();
				}
				
				String pNroConseho = "";
				if(cabecalho.getpNroConselho() != null) {
					pNroConseho = cabecalho.getpNroConselho();
				}
				
				cabecalho.setpLinhaCab2("Responsável Técnico: " + pChefe + "  "
						+ pConselho
						+ pNroConseho);

				cabecalho.setpLinhaCab3(cabecalho.getpDescUnidade());

				if (indImprimeChefe) {
					cabecalho.setpLinhaCab3(cabecalho.getpLinhaCab3() + _HIFEN_
							+ cabecalho.getpChefeUnidade());
				}

				// monta linhas do endereço

				String cep = centroCusto.getCep() != null ? CoreUtil
						.formataCEP(centroCusto.getCep()) : "";
				
				// Defeito em Desenvolvimento #37588				
				String logradouro = !StringUtils.isBlank(centroCusto.getLogradouro()) ? "Rua "+ambulatorioFacade.obterDescricaoCidCapitalizada(centroCusto.getLogradouro(), CapitalizeEnum.TODAS) : "";
				String nroLogradouro = getBlankWhenNull(centroCusto.getNroLogradouro(), ", ");
				String bairro = !StringUtils.isBlank(centroCusto.getBairro()) ? _HIFEN_+ambulatorioFacade.obterDescricaoCidCapitalizada(centroCusto.getBairro(), CapitalizeEnum.TODAS) : "";
				String complemento = getBlankWhenNull(centroCusto.getComplLogradouro(), _HIFEN_);
				String cepFormat = getBlankWhenNull(cep, _HIFEN_);
				cabecalho.setpLinhaCab4( 
					logradouro.concat(nroLogradouro).concat(bairro).concat(complemento).concat(cepFormat));

				// busca dados da cidade
				final AipCidades cid = centroCusto.getCidade();
				if (cid != null) {
					cabecalho.setpLinhaCab4(cabecalho.getpLinhaCab4()
							+ _HIFEN_
							+ ambulatorioFacade.obterDescricaoCidCapitalizada(
									cid.getNome(), CapitalizeEnum.TODAS)
							+ ", "
							+ cid.getAipUf().getSigla().toUpperCase()
							+ _HIFEN_
							+ ambulatorioFacade.obterDescricaoCidCapitalizada(cid
									.getAipUf().getPais().getNome()
									.toLowerCase()));
				}

				String fone = "";
				if (centroCusto.getFone() != null) {
					fone = centroCusto.getFone().toString();
					fone = fone.substring(0, 4) + "." + fone.substring(4);
				}

				String fax = "";
				if (centroCusto.getFax() != null) {
					fax = centroCusto.getFax().toString();
					fax = " - Telefax (55)(" + centroCusto.getDddFax() + ") "
							+ fax.substring(0, 4) + "." + fax.substring(4);
				}
				
				String ccFone = "";
				if(centroCusto.getDddFone() != null) {
					ccFone = "(55)(" + centroCusto.getDddFone() + ") ";
				}

				cabecalho
						.setpLinhaCab5("C.G.C.(M.F.): "
								+ aghuFacade.getCgc()
								+ _HIFEN_
								+ "Telefone "
								+ ccFone
								+ fone
								+ fax
								+ (centroCusto.getCaixaPostal() != null ? " - Caixa Postal "
										+ centroCusto.getCaixaPostal()
										: ""));

				if (centroCusto.getEmail() != null) {
					cabecalho
							.setpLinhaCab6("E-Mail: " + centroCusto.getEmail());
				}

				if (centroCusto.getHomePage() != null) {
					cabecalho.setpLinhaCab6(cabecalho.getpLinhaCab6()
							+ " - Home page: " + centroCusto.getHomePage());
				}

				if (aghuFacade.validarCaracteristicaDaUnidadeFuncional(
						unfSeq,
						ConstanteAghCaractUnidFuncionais.PATOLOGIA_CLINICA)) {
					cabecalho
							.setpLinhaCab7("\"Todo teste laboratorial deve ser correlacionado com o quadro clínico do paciente, sem o qual a interpretação do resultado é apenas relativa.\"");
				}

			}
		}

		return cabecalho;
	}
	
	private String getBlankWhenNull(Object obj, String compl) {
		if(obj != null && !StringUtils.isBlank(obj.toString())){
			return compl + obj.toString();
		}
		return StringUtils.EMPTY;
	}

}