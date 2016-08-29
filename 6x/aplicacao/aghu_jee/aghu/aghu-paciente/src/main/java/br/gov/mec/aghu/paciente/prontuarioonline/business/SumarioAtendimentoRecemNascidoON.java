package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoGestacao;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTamanhoForcipe;
import br.gov.mec.aghu.dominio.DominioTipoForcipe;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipHistoriaFamiliares;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoGestacaoPacientes;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoIntercorPasatus;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoProcReanimacao;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoReanimacaoRns;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipHistoriaFamiliaresDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.paciente.vo.DescricaoIntercorrenciaVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoMedicamentosVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoProfissionaisEnvolvidosVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoExamesMaeVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.view.VMcoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * @author aghu
 *
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class SumarioAtendimentoRecemNascidoON extends BaseBusiness {


@EJB
private SumarioAtendimentoRecemNascidoRN sumarioAtendimentoRecemNascidoRN;

@EJB
private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;

private static final Log LOG = LogFactory.getLog(SumarioAtendimentoRecemNascidoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipPesoPacientesDAO aipPesoPacientesDAO;

@Inject
private AipPacientesDAO aipPacientesDAO;

@EJB
private IProntuarioOnlineFacade prontuarioOnlineFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AipAlturaPacientesDAO aipAlturaPacientesDAO;

@Inject
private AipHistoriaFamiliaresDAO aipHistoriaFamiliaresDAO;

@Inject
private AipRegSanguineosDAO aipRegSanguineosDAO;

@EJB
private ICertificacaoDigitalFacade certificacaoDigitalFacade;

@Inject
private AipPacientesDAO pacienteDAO;

	private static final long serialVersionUID = -21289808809082518L;

	/**
	 * 
	 * @return
	 */
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	/**
	 * 
	 * @return
	 */
	protected AipHistoriaFamiliaresDAO getAipHistoriaFamiliaresDAO() {
		return aipHistoriaFamiliaresDAO;
	}

	/**
	 * 
	 * @return
	 */
	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}

	
	/**
	 * Acesso ao modulo certificacao digital 
	 * 
	 * @return
	 */
	public ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}

	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * Acesso ao modulo solicitacao exame
	 * @return
	 */
	public ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}
	
	/**
	 * 
	 * @return
	 */
	private SumarioAtendimentoRecemNascidoRN getSumarioAtendimentoRecemNascidoRN() {
		return sumarioAtendimentoRecemNascidoRN;
	}
	
	private RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON() {
		return relExameFisicoRecemNascidoPOLON;
	}
	
	/**
	 * Acesso ao modulo prescricao
	 * @return
	 */
	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	/**
	 * Monta o relatório da estória #15838
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public SumarioAtdRecemNascidoSlPartoVO montarRelatorio(Integer atdSeq, Integer pacCodigo, Integer conConsulta, Short gsoSeqp, Byte rnaSeqp) throws ApplicationBusinessException {

		SumarioAtdRecemNascidoSlPartoVO vo = new SumarioAtdRecemNascidoSlPartoVO();
		
		//Parametros
		vo.setPacCodigo(pacCodigo);
		vo.setConNumero(conConsulta);
		vo.setGsoSeqp(gsoSeqp);
		vo.setAtdSeq(atdSeq);
		vo.setSeqp(rnaSeqp);
		
		//Carrega as consultas que serão utilizadas para preencher o vo
		carregarConsultas(vo,rnaSeqp);
		
		preencherDadosGestacao(vo);
		preencherDadosParto(vo);
		preencherDadosGestacoesAnteriores(vo);
		preencherDadosPacienteRecemNascido(vo);
		preencherDadosHistoricoFamiliar(vo);
		preencherDadosAltura(vo);
		preencherDadosForcipe(vo);
		preencherDadosNascimentoIndicacoes(vo);
		preencherDadosPesoPaciente(vo);
		preencherDadosSolicitacaoExame(vo);
		
		preencherDadosMonitoramento(vo);
		preencherDadosRecemNascido(vo);		
		preencherDadosResultadoExamesSignifs(vo);
		preencherDadosNascimento(vo);
		preencherDadosMensagemCordao(vo);
		preencherDadosCesariana(vo);
		preencherDadosMedicamentosExpansores(vo);
		preencherDadosIntercorrenciaGestacao(vo); //q_ing
		preencherDadosIntercorPasatus(vo); //q_opa1 
		preencherDadosIntercorrenciaGestacaoPassadas(vo); //q_passadas
		/*preencherDadosIntercorrenciasPassadas(vo);
		preencherListaDadosIntercorrenciaPassada(vo);*/
		preencherDadosExames(vo);
		preencherDadosNotaAdicional(vo);
		preencherDadosProfissionaisEnvolvidos(vo);
		
		preencherDadosNascIndicacoes(vo); //Q1
		preencherDadosIndicacaoNascimentos(vo); // Q_IND_NASC
		preencherDadosAtendimentos(vo); //Q_ATD
		
		preencherDadosCesarianaIndicacao(vo);
		obterFraseMonitoramentos(vo);
		preencherCampoInformacoesComplementaresRN(vo); // procedure CF_VIRGULA_3
		
		preencherDadosReanimacao(vo);
		obterReanimacao(vo);
		obterClassificacaoMcoNascimento(vo);
		verificarExibicaoIdadeGestacao(vo);
		
		return vo;
	}

	private void carregarConsultas(SumarioAtdRecemNascidoSlPartoVO vo, Byte rnaSeqp) {
		McoGestacoesId chavePrimaria = new McoGestacoesId();
		chavePrimaria.setPacCodigo(vo.getPacCodigo());
		chavePrimaria.setSeqp(vo.getGsoSeqp());
		
		McoGestacoes gestacao = getPerinatologiaFacade().obterMcoGestacoes(chavePrimaria);
		
		vo.setGestacao(gestacao);
		if(gestacao.getMcoRecemNascidoses() != null && !gestacao.getMcoRecemNascidoses().isEmpty()){	
			for(McoRecemNascidos recemNascido : gestacao.getMcoRecemNascidoses()){
				if(recemNascido.getId().getSeqp().equals(rnaSeqp)){
					vo.setRecemNascido(recemNascido);
					//Nascimento... Q_NASCIMENTO
					Byte seqp = vo.getRecemNascido().getId().getSeqp();
					McoNascimentos nasc = getPerinatologiaFacade().obterMcoNascimento(seqp.intValue(),vo.getPacCodigo(), vo.getGsoSeqp().shortValue());
					vo.setNascimento(nasc);
				}
			}
		}
	}

	/**
	 * Estória #15838
	 * 
	 * Q_PAC
	 * 
	 * @param vo
	 * @author bruno.mourao
	 * @since 07/08/2012
	 */
	private void preencherDadosParto(SumarioAtdRecemNascidoSlPartoVO vo) {
		/*
		 * Busca e preenche os seguintes campos do relatório
		 * 5 - NOME_PAC
		 * 6 - PRONTUARIO
		 * 7 - IDADE
		 * 9 - DTHR_ATENDIMENTO
		 * 11 - CONVENIO
		 * 49 - FORMA_RUPTURA
		 * 50 - DTHR_ROMPIMENTO
		 * 52 - LIQUIDO_AMINIOTICO
		 * 53 - ODOR
		 * 54 - IND_AMNIOSCOPIA
		 */
		
		McoGestacoes gestacao = vo.getGestacao();
		
		if(gestacao != null){
			AipPacientes mae =  getAipPacientesDAO().obterPorChavePrimaria(vo.getPacCodigo());
			
			vo.setNomeMae(mae.getNome());
			vo.setProntuarioMae(CoreUtil.formataProntuario(mae.getProntuario()));
			
			if(gestacao.getMcoAnamneseEfses() != null){
				//Obtem a data do atendimento
				for(McoAnamneseEfs efi : gestacao.getMcoAnamneseEfses()){
					if(efi.getConsulta().getNumero().equals(vo.getConNumero())){
						vo.setDtAtendimentoMae(efi.getDthrConsulta());
					}
				}
			}
			
			McoBolsaRotas bolsaRota = gestacao.getBolsaRota();
			
			if(bolsaRota != null){
				vo.setFormaRuptura(bolsaRota.getFormaRuptura());
				vo.setDtHrRompimento(bolsaRota.getDthrRompimento());
				if (bolsaRota.getLiquidoAmniotico() != null) {
					vo.setLiquidoAminiotico(bolsaRota.getLiquidoAmniotico().getDescricao());
				}
				if(bolsaRota.getIndOdorFetido()){
					vo.setOdorLiquido("odor fétido");
				}
				if(bolsaRota.getIndAmnioscopia()){
					vo.setIndAmnioscopia("Amnioscopia");
				}
			}
			//idade
			Date criadoEm = vo.getRecemNascido() != null ? vo.getRecemNascido().getCriadoEm() : null;
			vo.setIdadeMae(obterIdadeRecemNascido(mae.getDtNascimento(), criadoEm));
			
			//Convenio
			vo.setConvenioMae(getSumarioAtendimentoRecemNascidoRN().obterDescricaoConvenio(vo.getPacCodigo(), vo.getConNumero()));
		}
		
	}

	/**
	 * Estória #15838
	 * 
	 * Q_ALTURA
	 * 
	 * @param vo
	 * @author bruno.mourao
	 * @since 06/08/2012
	 */
	private void preencherDadosAltura(SumarioAtdRecemNascidoSlPartoVO vo) {
		AipAlturaPacientes alturaMae = getAipAlturaPacientesDAO().obterAlturaPorNumeroConsulta(vo.getConNumero());
		if(alturaMae != null && alturaMae.getAltura() != null && alturaMae.getAltura().compareTo(BigDecimal.ZERO) > 0) {
			vo.setAlturaMae(AghuNumberFormat.formatarNumeroMoeda(alturaMae.getAltura().divide(new BigDecimal(100),2,BigDecimal.ROUND_CEILING).doubleValue()).concat(" m"));
		}
	}

	private AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	/**
	 * Estoria #15838
	 * 
	 * Q_PAC_RN + tratamento decodes
	 * 
	 * @param vo
	 */
	private void preencherDadosPacienteRecemNascido(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		AipPacientes paciente = null;
		
		if(vo.getRecemNascido() != null){
			paciente =  pacienteDAO.obterPorChavePrimaria(vo.getRecemNascido().getPaciente().getCodigo());
		}
		
		if(paciente != null) {
			
			vo.setNome(paciente.getNome());
			//vo.setPacCodigoRN(paciente.getCodigo());
			if (paciente.getProntuario() != null) {
				vo.setProntuario(CoreUtil.formataProntuario(paciente.getProntuario().toString()));
			}
			vo.setDtHrNascimento(paciente.getDtNascimento());
			
			// sexo
			if(paciente.getSexo() != null) {
				if(DominioSexo.M.equals(paciente.getSexo())) {
					vo.setSexo(DominioSexo.M.getDescricao());
				} else {
					vo.setSexo(DominioSexo.F.getDescricao());
				}	
			}
			// cor
			if(paciente.getCor() != null) {
				if(DominioCor.B.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.B.getDescricao());
				}
				if(DominioCor.P.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.P.getDescricao());
				}
				if(DominioCor.M.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.M.getDescricao());
				}
				if(DominioCor.A.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.A.getDescricao());
				}
				if(DominioCor.I.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.I.getDescricao());
				}
				if(DominioCor.O.equals(paciente.getCor())) {
					vo.setCorRecemNascido(DominioCor.O.getDescricao());
				}
			}
		}
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * Utilizado pela estoria #15838
	 * @autor fausto.trindade
	 * Q_RECEM_NASCIDOS
	 * Recupera dados recem nascido.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void preencherDadosRecemNascido(SumarioAtdRecemNascidoSlPartoVO vo){
		McoRecemNascidos recemNascido = vo.getRecemNascido();
		
		if(recemNascido != null){
		
			vo.setObservacaoRecemNascido(recemNascido.getObservacao());
			
			if(recemNascido.getIndEvacuou() != null && recemNascido.getIndEvacuou()){
				vo.setEvacuou("evacuou");
			}
			
			if(recemNascido.getIndSurfactante() != null && recemNascido.getIndSurfactante()){
				vo.setSurfactante("surfactante");
			}
			
			if(recemNascido.getIndLavadoGastrico() != null && recemNascido.getIndLavadoGastrico()){
				vo.setLavadoGastrico("lavado gástrico");
			}
			
			if(recemNascido.getIndAmamentado() != null && recemNascido.getIndAmamentado()){
				vo.setAmamentado("RN amamentado na 1 hora de vida  ");
			}
			
			if(recemNascido.getIndUrinou() != null && recemNascido.getIndUrinou()){
				vo.setUrinou("urinou");
			}
			
			if(recemNascido.getAspGastrVol() != null){
				vo.setVolGastrico(recemNascido.getAspGastrVol().toString().concat(" ml "));
			}
			
			if(recemNascido.getAspGastrAspecto() != null){
				vo.setAspectoGastr( recemNascido.getAspGastrAspecto().getDescricao());
			}
			
			if(recemNascido.getIndAspGastrOdorFetido() != null && recemNascido.getIndAspGastrOdorFetido()){
				vo.setOdorFetidoGastr("odor fétido");
			}
			
			if(recemNascido.getIndAspiracaoTet() != null && recemNascido.getIndAspiracaoTet()){
				vo.setAspiracaoTet("Aspiracao por TET");
			}
			
			if(recemNascido.getIndO2Inalatorio() != null && recemNascido.getIndO2Inalatorio()){
				vo.setInalatorio("02 inalatório");
			}
			
			if(recemNascido.getIndVentilacaoPorMascara() != null && recemNascido.getIndVentilacaoPorMascara()){
				vo.setVentPorMascara("ventilação por máscara");
			}
			
			if(recemNascido.getIndMassCardiacaExt() != null && recemNascido.getIndMassCardiacaExt()){
				vo.setMassaCardiacaExt("massagem cardíaca externa");
			}
			
			if(recemNascido.getIndAspiracao() != null && recemNascido.getIndAspiracao()){
				vo.setAspiracao("Aspiracao");
			}
			
			if(recemNascido.getIndObitoCo() != null && recemNascido.getIndObitoCo()){
				vo.setIndObito("ÓBITO EM CENTRO OBSTÉTRICO");
			}
			
			if(recemNascido.getApgar1() != null){
				vo.setApgarUmMin(recemNascido.getApgar1().toString());
			}
			
			if(recemNascido.getApgar5() != null){
				vo.setApgarCincoMin(recemNascido.getApgar5().toString());
			}
			
			if(recemNascido.getApgar10() != null){
				vo.setApgarDezMin(recemNascido.getApgar1().toString());
			}
			
			StringBuilder tempo = new StringBuilder();
			if (recemNascido.getDiasRuptBolsa() != null && recemNascido.getDiasRuptBolsa() > 0) {
				tempo.append(recemNascido.getDiasRuptBolsa()).append("d ");
			}
			if (recemNascido.getHrsRuptBolsa() != null && recemNascido.getHrsRuptBolsa() > 0) {
				tempo.append(recemNascido.getHrsRuptBolsa()).append("h ");
			}
				
			if (recemNascido.getMinRuptBolsa() != null && recemNascido.getMinRuptBolsa() > 0) {
				tempo.append(recemNascido.getMinRuptBolsa()).append("min");
			}
			vo.setTempoBolsaRota(tempo.toString());
		}
	}	

	/**
	 * Estoria #15838
	 * 
	 * Q_GPA + tratamento decodes
	 * 
	 * @param vo
	 */
	private void preencherDadosGestacoesAnteriores(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		List<McoGestacaoPacientes> lista = getPerinatologiaFacade().listarGestacoesPacientePorCodigoPaciente(vo.getPacCodigo());
		
		if(lista != null) {
			
			List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores = new ArrayList<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO>();
			
			for(McoGestacaoPacientes gestacao : lista) {
				
				if(gestacao != null) {
					// ina_seq
//					vo.setGpaInaSeq(gestacao.getMcoIndicacaoNascimento().getSeq());
					
					SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO gestacaoAnterior = new SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO();
					gestacaoAnterior.setComplicacoes(gestacao.getComplicacoes());
					gestacaoAnterior.setAno(gestacao.getAno());
					
					if(gestacao.getMcoIndicacaoNascimento() != null){
						gestacaoAnterior.setInaDescricao(gestacao.getMcoIndicacaoNascimento().getDescricao());
					}
					
					if (gestacao.getRnPeso() != null) {
						StringBuilder sb = new StringBuilder();
						sb.append(gestacao.getRnPeso()).append(" g");
						gestacaoAnterior.setPeso(sb.toString());
					}
					
					// classificacao gestacao
					if(gestacao.getClassificacao() != null) {
						gestacaoAnterior.setClassificacao(gestacao.getClassificacao().getDescricao());
					}
					
					// classificacao RN
					if(gestacao.getRnClassificacao() != null) {
						if(DominioRNClassificacaoGestacao.NAV.equals(gestacao.getRnClassificacao())) {
							// Situação Gestacao Anterior
							gestacaoAnterior.setSituacao(DominioRNClassificacaoGestacao.NAV.getDescricao());
						}
						if(DominioRNClassificacaoGestacao.NAM.equals(gestacao.getRnClassificacao())) {
							// Situação Gestacao Anterior
							gestacaoAnterior.setSituacao(DominioRNClassificacaoGestacao.NAM.getDescricao());
						}					
					}
					
					gestacoesAnteriores.add(gestacaoAnterior);
				}
			}
			vo.setGestacoesAnteriores(gestacoesAnteriores);
		}
	}
	
	
	/**
	 * Estoria #15838
	 * 
	 * Q_HIF + tratamento decodes
	 * 
	 * @param vo
	 */
	private void preencherDadosHistoricoFamiliar(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		AipHistoriaFamiliares historico = getAipHistoriaFamiliaresDAO().obterPorChavePrimaria(vo.getPacCodigo());
		
		if(historico != null) {
				
			// mae
			if(DominioSimNao.S.equals(historico.getIndMaePeE())) {
				vo.setAntecedenteMae(getResourceBundleValue("MSG_MAE_GESTANTE_JA_TEVE_PRE_ECLAMPSIA_OU_ECLAMPSIA"));
			}
			// irma
			if(DominioSimNao.S.equals(historico.getIndIrmaPeE())) {
				vo.setAntecedenteIrma(getResourceBundleValue("MSG_IRMA_GESTANTE_JA_TEVE_PRE_ECLAMPSIA_OU_ECLAMPSIA"));
			}
			// diabetes
			if(DominioSimNao.S.equals(historico.getIndDiabeteNaFamilia())) {
				vo.setDiabeteFamilia(getResourceBundleValue("MSG_DIABETE_NA_FAMILIA"));
			}
			// doencas congenitas
			if(DominioSimNao.S.equals(historico.getIndDoencasCongenitas())) {
				vo.setDoencasCongenitas(getResourceBundleValue("MSG_DOENCAS_CONGENITAS_NA_FAMILIA"));
			}
			// observacao
			if(StringUtils.isNotBlank(historico.getObservacao())) {
				vo.setHifObservacao(historico.getObservacao());
			}
		}
	}
		
	
	/**
	 * Estoria #15838
	 * 
	 * Q_FORCIPE + tratamento decodes
	 * 
	 * @param vo
	 */
	private void preencherDadosForcipe(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		McoForcipes forcipe = getPerinatologiaFacade().obterForcipe(vo.getPacCodigo(), vo.getGsoSeqp(), vo.getSeqp().intValue());
		
		if(forcipe != null) {
			
			// forcipe com rotacao
			if(forcipe.getIndForcipeComRotacao()) {
				vo.setIndForcipeRotacao(getResourceBundleValue("MSG_FORCIPE_COM_ROTACAO"));
			}
			
			// tipo
			if(forcipe.getTipoForcipe() != null) {
				if(DominioTipoForcipe.S.equals(forcipe.getTipoForcipe())) {
					vo.setTipoForcipe(DominioTipoForcipe.S.getDescricao());
				}
				if(DominioTipoForcipe.P.equals(forcipe.getTipoForcipe())) {
					vo.setTipoForcipe(DominioTipoForcipe.P.getDescricao());
				}
				if(DominioTipoForcipe.K.equals(forcipe.getTipoForcipe())) {
					vo.setTipoForcipe(DominioTipoForcipe.K.getDescricao());
				}
			}
			// tamanho
			if(forcipe.getTamanhoForcipe() != null) {
				if(DominioTamanhoForcipe.M.equals(forcipe.getTamanhoForcipe())) {
					vo.setTipoForcipe(vo.getTipoForcipe().concat(" ").concat(DominioTamanhoForcipe.M.getDescricao()));
					vo.setTamanhoForcipe(DominioTamanhoForcipe.M.getDescricao());
				}
				if(DominioTamanhoForcipe.B.equals(forcipe.getTamanhoForcipe())) {
					vo.setTipoForcipe(vo.getTipoForcipe().concat(" ").concat(DominioTamanhoForcipe.B.getDescricao()));
					vo.setTamanhoForcipe(DominioTamanhoForcipe.B.getDescricao());
				}
			}
		}
	}

	
	/**
	 * Utilizado pela estoria #15838
	 * @autor fausto.trindade
	 * Q_RXS
	 * Recupera resultado de exames.
	 */
	private void preencherDadosResultadoExamesSignifs(SumarioAtdRecemNascidoSlPartoVO vo){
		List<McoResultadoExameSignifs> itens = new ArrayList<McoResultadoExameSignifs>();
		itens = getPerinatologiaFacade().listarResultadosExamesSignifsPorCodigoPacienteSeqpGestacao(vo.getPacCodigo(), vo.getGsoSeqp());
		for(McoResultadoExameSignifs tmp : itens){
			SumarioAtdRecemNascidoSlPartoExamesMaeVO partoExamesMae = new SumarioAtdRecemNascidoSlPartoExamesMaeVO();
			//Item 44 RXS_DATA_REALIZACAO de Q_RXS
			partoExamesMae.setDtExame(tmp.getDataRealizacao());
			//Item 46 RXS_RESULTADO de Q_RXS
			partoExamesMae.setResultado(tmp.getResultado());
			// RXS_CHAVE - parametro utilizado na query Q_VEX
			StringBuilder rxsChave = new StringBuilder();
			if(tmp.getEmaExaSigla() != null){
				rxsChave.append(tmp.getEmaExaSigla());
			}else{
				rxsChave.append('0');
			}
			
			if(tmp.getEmaManSeq() != null){
				rxsChave.append(tmp.getEmaManSeq()); 
			}else{
				rxsChave.append('0');
			}
			
			if(tmp.getExameExterno() != null){
				rxsChave.append(tmp.getExameExterno().getSeq());
			}else{
				rxsChave.append('0');
			}

			partoExamesMae.setRxsChave(rxsChave.toString());
			
			vo.getExamesMae().add(partoExamesMae);
		}		
	}
	
	
	/**
	 * Estoria #15838
	 * 
	 * Q_NASC_INDIC
	 * 
	 * @param vo
	 */
	private void preencherDadosNascimentoIndicacoes(SumarioAtdRecemNascidoSlPartoVO vo) {
		List<McoNascIndicacoes> nascimentos = getPerinatologiaFacade().pesquisarNascIndicacoesPorForcipes(vo.getPacCodigo(), vo.getGsoSeqp(), vo.getSeqp().intValue());
		if(nascimentos != null && !nascimentos.isEmpty()) {
			vo.setNaiSeq(nascimentos.get(0).getSeq());
			vo.setNaiInaSeq(nascimentos.get(0).getIndicacaoNascimento().getSeq());
		}
	}
	
	/**
	 * Utilizado pela estoria #15838
	 * @autor fausto.trindade
	 * Q_NASCIMENTO
	 * Recupera dados do nascimento.
	*/
	@SuppressWarnings("PMD.NPathComplexity")
	private void preencherDadosNascimento(SumarioAtdRecemNascidoSlPartoVO vo){
		
		McoNascimentos nasc = vo.getNascimento();
		
		if(nasc != null){
			
			// DURACAO_PARTO - CAMPO 62
			vo.setDuracaoParto(obterDuracaoDoParto(nasc.getId().getGsoPacCodigo(),
												   nasc.getId().getGsoSeqp(),
												   nasc.getPeriodoDilatacao(),
												   nasc.getPeriodoExpulsivo(),
												   nasc.getDthrNascimento()));
			
			//NAS_DTHR_NASCIMENTO
			if(nasc.getDthrNascimento() != null){
				vo.setDtNascimento(nasc.getDthrNascimento());
			}
			//NAS_TIPO_NASCIMENTO
			if(nasc.getTipo() != null){
				vo.setTipoNascimento(nasc.getTipo().getDescricao());
			}
			//NAS_RN_CLASSIFICACAO
			if(nasc.getRnClassificacao() != null){
				vo.setClassificacaoRecemNascido(nasc.getRnClassificacao().getDescricao());
			}
			//NAS_MODO
			if(nasc.getModo() != null){
				vo.setModoNascimento(nasc.getModo().getDescricao());
			}
			if(nasc.getPeriodoDilatacao() != null && nasc.getPeriodoDilatacao().intValue() != 0){
				//NAS_PERIODO_DILATACAO				
				vo.setPeriodoDilatacao(nasc.getPeriodoDilatacao());
				//PERIODO_DILATACAO
				vo.setNasPeriodoDilatacao(montarPeriodo(nasc.getPeriodoDilatacao()));
			}
			
			if(nasc.getPeriodoExpulsivo() != null && nasc.getPeriodoExpulsivo().intValue() != 0){
				//NAS_PERIODO_EXPULSIVO
				vo.setPeriodoExpulsivo(nasc.getPeriodoExpulsivo());
				//PERIODO_EXPULSIVO
				vo.setNasPeriodoExpulsivo(montarPeriodo(nasc.getPeriodoExpulsivo()));
			}
			
			//NAS_PESO_PLACENTA
			if(nasc.getPesoPlacenta() != null){
				vo.setPesoPlacenta(nasc.getPesoPlacenta().toString());
			}
			//NAS_COMP_CORDAO
			if(nasc.getCompCordao() != null){
				vo.setComprimentoCordao(nasc.getCompCordao().toString());
			}
			//EPISIOTOMIA
			if(nasc.getIndEpisotomia() != null && nasc.getIndEpisotomia()){	
				vo.setEpisiotomia("Episiotomia realizada");
			}
			//obs_nas
			if(nasc.getObservacao() != null){
				vo.setObservacaoNascimento(nasc.getObservacao());
			}
		}
	}

	private String montarPeriodo(Short periodo) {
		StringBuilder periodoRet = new StringBuilder();
		String strPerido = StringUtils.leftPad(periodo.toString(),4,'0');
		periodoRet.append(strPerido.substring(0, 2)).append(':')
		.append(strPerido.substring(2, 4)).append('h');
		return  periodoRet.toString();
	}
	
	/**
	 * Utilizado pela estoria #15838
	 * @autor fausto.trindade
	 * Q_ATEND_TRAB_PARTO
	 * Recupera dados monitoramento.
	 */
	private void preencherDadosMonitoramento(SumarioAtdRecemNascidoSlPartoVO vo){
		McoAtendTrabPartos mcoAtend = getPerinatologiaFacade().obterAtendTrabPartos(vo.getPacCodigo(), vo.getGsoSeqp());	
	
		if(mcoAtend != null){
			//TBP_TAQUICARDIA
			if(mcoAtend.getIndTaquicardia()){
				vo.setTaquicardia("Taquicardia");
			}
			//TBP_SEM_ACELER_TRANS
			if(mcoAtend.getSemAceleracaoTransitoria()){
				vo.setSemAcelerarTrans("Sem aceleração transitória");
			}
			//TBP_ANALGESIA_BPD
			if(mcoAtend.getIndicadorAnalgediaBpd()){
				vo.setAnalgesiaBpd("Analgesia BPD");
			}
			//TBP_ANALGESIA_BSD 
			if(mcoAtend.getIndicadorAnalgediaBsd()){
				vo.setAnalgesiaBsd("Analgesia BSD");
			}
			//TBP_VAR_BATIDA_MENOR10 
			if(mcoAtend.getVariabilidadeBatidaMenorQueDez()){
				vo.setVarBatidaMenor10("Variabilidade batida a batida menor do que dez");
			}
			//TBP_CARDIOTOCOGRAFIA  
			if(mcoAtend.getCardiotocografia() != null){
				vo.setCardiotocografia(mcoAtend.getCardiotocografia().getDescricao());
			}
		}	
	}

	/**
	 * Estoria #15838
	 * 
	 * Q_PEP
	 * Q_PESO
	 * 
	 * @param vo
	 */
	private void preencherDadosPesoPaciente(SumarioAtdRecemNascidoSlPartoVO vo) {
		//Q_PEP (peso do Recem Nascido)
		if (vo.getRecemNascido() != null
				&& vo.getRecemNascido().getPaciente().getCodigo() != null) {
			AipPesoPacientes pesoPaciente = getAipPesoPacientesDAO()
					.obterAipPesoPaciente(
							vo.getRecemNascido().getPaciente().getCodigo(),
							DominioMomento.N);
			if (pesoPaciente != null) {
				StringBuilder sb = new StringBuilder();
				Integer peso = (int) (pesoPaciente.getPeso().doubleValue() * 1000);
				sb.append(peso.toString()).append(" g");
				vo.setPesoRecemNascido(sb.toString());
			}
		}
		
		//Q_PESO (peso da mãe)
		AipPesoPacientes pesoMae = getAipPesoPacientesDAO().obterPesoPacientesPorNumeroConsulta(vo.getConNumero());
		if(pesoMae != null && pesoMae.getPeso() != null){
			vo.setPesoMae(pesoMae.getPeso().toString().replace(".", ",").concat(" kg"));
		}
	}	
	
	/**
	 * Estoria #15838
	 * @autor fausto.trindade
	 * Q2
	 * Recupera descrição cesariana indicação.
	 */
	private void preencherDadosCesarianaIndicacao(SumarioAtdRecemNascidoSlPartoVO vo){
		McoIndicacaoNascimento indNasc = getPerinatologiaFacade().obterMcoIndicacaoNascimentoPorChavePrimaria(vo.getNaiInaSeq());	
		if(indNasc != null){
			//INA_DESC_IND_CESAREANA
			vo.setCesarianaIndicacao(indNasc.getDescricao());
		}	
	}
	
	/**
	 * Estoria #15838
	 * 
	 * Q_SOE
	 * 
	 * @param vo
	 */
	private void preencherDadosSolicitacaoExame(SumarioAtdRecemNascidoSlPartoVO vo) {		
		AelSolicitacaoExames solicitacaoExame = getSolicitacaoExameFacade().obterAelSolicitacaoExamePorAtdSeq(vo.getAtdSeq());
		if(solicitacaoExame != null){
			vo.setSoeNroSolExa(solicitacaoExame.getSeq());
		}
	}	
	
	/**
	 * Estoria #15838
	 * 
	 * Q_ING
	 * @param vo
	 */
	private void preencherDadosIntercorrenciaGestacao(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		List<McoIntercorrenciaGestacoes> lista = getPerinatologiaFacade().listarIntercorrenciasGestacoesPorCodGestCodPaciente(
				vo.getGsoSeqp(), vo.getPacCodigo(), DominioOcorrenciaIntercorrenciaGestacao.A);
			if( lista.size() > 0) {
				McoIntercorrenciaGestacoes interGestacao = lista.get(0);
	            if(interGestacao.getId().getOpaSeq() !=  null) {
	            	vo.setMcoInterGesOpaSeq(interGestacao.getId().getOpaSeq());
	            }
	            
	            if(interGestacao.getOcorrencia() !=  null) {
	            	vo.setMcoInterGesOcorrencia(interGestacao.getOcorrencia());
	            }
	            
	            if(interGestacao.getComplemento() != null) {
	            	vo.setMcoInterGesComplemento(interGestacao.getComplemento());
	            	vo.setComplementoInterAtual(interGestacao.getComplemento());
	            }
			}
		else{
			List<DescricaoIntercorrenciaVO> listaIntercorrenciaPassada = new ArrayList<DescricaoIntercorrenciaVO>();
			
			for (McoIntercorrenciaGestacoes itemIntercorrenciaPassada : lista){
				DescricaoIntercorrenciaVO voIntercorrencia = new DescricaoIntercorrenciaVO();
				if(itemIntercorrenciaPassada.getComplemento() != null){
					voIntercorrencia.setComplemento(itemIntercorrenciaPassada.getComplemento());
				}
				// REGRA DA CONSULTA Q_OPA
				McoIntercorPasatus obj = getPerinatologiaFacade().obterMcoIntercorPasatusPorChavePrimaria(itemIntercorrenciaPassada.getId().getOpaSeq().intValue());
				
				voIntercorrencia.setDescricao(obj.getDescricao());			
				listaIntercorrenciaPassada.add(voIntercorrencia);
			}
			
			vo.setIntercorrenciasPassadas(listaIntercorrenciaPassada);
		}
	}
	
	/**
	 * Estoria #15838
	 * 
	 * Q_PASSADAS
	 * @param vo
	 */
	private void preencherDadosIntercorrenciaGestacaoPassadas(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		List<McoIntercorrenciaGestacoes> lista = getPerinatologiaFacade().listarIntercorrenciasGestacoesPorCodGestCodPaciente(
				vo.getGsoSeqp(), vo.getPacCodigo(), DominioOcorrenciaIntercorrenciaGestacao.P);
		
		if( lista.size() > 0) {
			McoIntercorrenciaGestacoes interGestacao = lista.get(0);
            if(interGestacao.getId().getOpaSeq() !=  null) {
            	vo.setMcoInterGesOpaSeqPassada(interGestacao.getId().getOpaSeq());
            }
            
            if(interGestacao.getOcorrencia() !=  null) {
            	vo.setMcoInterGesOcorrenciaPassada(interGestacao.getOcorrencia());
            }
            
            if(interGestacao.getComplemento() != null) {
            	vo.setMcoInterGesComplementoPassada(interGestacao.getComplemento());
            }
		}
		
	}
	
	/**
	 * Estoria #15838
	 * Q3
	 */
	private void preencherDadosMensagemCordao(SumarioAtdRecemNascidoSlPartoVO vo) throws ApplicationBusinessException{
		
		AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_SANGUE_CORDAO);
		
		List<AelItemSolicitacaoExames> msgCordao = getSolicitacaoExameFacade().obterAelItemSolicitacaoExamesPorUfeEmaManSeqSoeSeq(param.getVlrNumerico().intValue(), vo.getSoeNroSolExa());
		
		if(msgCordao != null && !msgCordao.isEmpty()){
			//MENSAGEM_CORDAO
			vo.setMensagemCordao("SOLICITADO EXAME DE SANGUE DO CORDÃO");
		}
	}
	
	/**
	 * Estoria #15838
	 * Q_CESARIANAS
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void preencherDadosCesariana(SumarioAtdRecemNascidoSlPartoVO vo) {
		if(vo.getNascimento() != null){
			McoCesarianas cesariana = getPerinatologiaFacade().obterMcoCesarianasPorChavePrimaria(vo.getNascimento().getId());
			if(cesariana != null){
				//CSR_HR_DURACAO
				if(cesariana.getHrDuracao() != null){
					vo.setDuracaoCesaria(montarPeriodo(cesariana.getHrDuracao()));
				}
				//CSR_LAPAROTOMIA
				if(cesariana.getLaparotomia() != null){
					vo.setLaparotomia(cesariana.getLaparotomia().getDescricao());
				}
				//CSR_HISTEROTOMIA
				if(cesariana.getHisterotomia() != null){
					vo.setHisterotomia(cesariana.getHisterotomia().getDescricao());
				}
				//CSR_HISTERORRAFIA
				if(cesariana.getHisterorrafia() != null){
					vo.setHisterorrafia(cesariana.getHisterorrafia().getDescricao());
				}
				//CSR_CONTAMINACAO
				if(cesariana.getContaminacao() != null){
					vo.setContaminacao(cesariana.getContaminacao().getDescricao());
				}
				
				//CSR_IND_LAQUEADURA_TUBARIA
				if(cesariana.getIndLaqueaduraTubaria() != null && cesariana.getIndLaqueaduraTubaria()){
					vo.setIndLaqueaduraTubaria("Laqueadura Tubaria");
				}
				
				//CSR_IND_RAFIA_PERITONIAL
				if(cesariana.getIndRafiaPeritonial() != null && cesariana.getIndRafiaPeritonial()){
					vo.setIndRafiaPeritonial("Rafia Peritonial");
				}
				//CSR_IND_LAVAGEM_CAVIDADE,
				if(cesariana.getIndLavagemCavidade() != null && cesariana.getIndLavagemCavidade()){
					vo.setIndLavagemCavidade("Lavagem Cavidade");
				}
				//CSR_IND_DRENOS
				if(cesariana.getIndDrenos() != null && cesariana.getIndDrenos()){
					vo.setIndDrenos("Drenos");
				}
			}
		}
	}
	
	/**
	 * Estoria #15838
	 * Q_REANIMACAO_RNS
	 *
	 */
	private void preencherDadosMedicamentosExpansores(SumarioAtdRecemNascidoSlPartoVO vo){
		/*Reanimação segundo Lucas Samberg e uma lista que detem toda a listagem de medicamentos 
		utilizados em todas as reanimações do recem nascido*/
		vo.setMedicamentosRecemNascido(new ArrayList<SumarioAtdRecemNascidoMedicamentosVO>());
		for(McoReanimacaoRns rn : vo.getRecemNascido().getMcoReanimacaoRnses()){
			SumarioAtdRecemNascidoMedicamentosVO reanimacao = new SumarioAtdRecemNascidoMedicamentosVO();
			//Substituindo Q_PNI
			McoProcReanimacao procRea = getPerinatologiaFacade().obterMcoProcReanimacaoPorId(rn.getId().getPniSeq());
			
			reanimacao.setDescricaoPni(procRea.getDescricao());
			reanimacao.setDoseRnr(rn.getDose());
			reanimacao.setUnidadeRnr(rn.getUnidade());
			reanimacao.setVadSiglaRnr(rn.getVadSigla());
			vo.getMedicamentosRecemNascido().add(reanimacao);
		}		
		
	}
	

	
	/**
	 * Estoria #15838
	 * 
	 * Q_GESTACAO
	 * 
	 * @param vo
	 */
	private void preencherDadosGestacao(SumarioAtdRecemNascidoSlPartoVO vo) {
		McoGestacoes gestacao = vo.getGestacao();
		
		if(gestacao != null) {
			AipRegSanguineos regSanguineo = this.aipRegSanguineosDAO.obterRegSanguineosPorCodigoPaciente(
					gestacao.getId().getPacCodigo(), gestacao.getId().getSeqp().byteValue());
			
			vo.setGesta(gestacao.getGesta());
			vo.setPara(gestacao.getPara());
			vo.setCesarea(gestacao.getCesarea());
			vo.setAborto(gestacao.getAborto());
			vo.setEctopica(gestacao.getEctopica());
			vo.setGemelar(gestacao.getGemelar());
			vo.setDum(gestacao.getDtUltMenstruacao());
			vo.setDtPrimeiraEco(gestacao.getDtPrimEco());
			vo.setObservacao(gestacao.getObsExames());
			
			if (gestacao.getUsoMedicamentos() != null) {
				vo.setUsoMedicamento("Medicamentos : " + gestacao.getUsoMedicamentos());
			}
			
			preencherIdadeGestacionalIdadePrimeiraEco(vo, gestacao);

			vo.setDtInformadaIG(DateUtil.dataToString(gestacao.getDtInformadaIg(),"dd/MM/yyyy"));
			vo.setNroConsultasPreNatal(gestacao.getNumConsPrn());
			vo.setDtPrimeiraConsulta(gestacao.getDtPrimConsPrn());
			StringBuilder sb = new StringBuilder();
			if(gestacao.getGrupoSanguineoPai() != null){
				sb.append(gestacao.getGrupoSanguineoPai());
			}
			if(gestacao.getFatorRhPai() != null){
				sb.append(gestacao.getFatorRhPai());
			}
			if(!StringUtils.isEmpty(sb.toString())){
				vo.setTipoSanguePai(sb.toString());
			}
			if(regSanguineo != null)  {
				vo.setTipoSangueMae(regSanguineo.getGrupoSanguineo() + regSanguineo.getFatorRh());
				if(regSanguineo.getCoombs() != null && StringUtils.equals("-", regSanguineo.getFatorRh())) {
					vo.setCoombs(regSanguineo.getCoombs().getDescricao());
				}
			}
		}
	}

	private void preencherIdadeGestacionalIdadePrimeiraEco(SumarioAtdRecemNascidoSlPartoVO vo, McoGestacoes gestacao) {
		int dias = gestacao.getIgPrimEcoDias() != null ? gestacao.getIgPrimEcoDias() : 0;
		int semanas = gestacao.getIgPrimEco() != null ? gestacao.getIgPrimEco() : 0;
		
		vo.setIdadeGestPrimeiraEco(semanas + " semanas /" + dias + " dias");

		semanas = gestacao.getIgAtualSemanas() != null ? gestacao.getIgAtualSemanas() : 0 ;
		dias = gestacao.getIgAtualDias() != null ? gestacao.getIgAtualDias() : 0;
		vo.setIdadeGestacionalInformada(semanas + " semanas / " + dias + " dias");
	}
	/**
	 * Estoria #15838
	 * 
	 * Q_OPA1
	 * 
	 * @param vo
	 */
	private void preencherDadosIntercorPasatus(SumarioAtdRecemNascidoSlPartoVO vo) {
		if(vo.getMcoInterGesOpaSeq() != null){
			List<McoIntercorPasatus> lista = getPerinatologiaFacade().listarIntercorPasatusPorSeq(vo.getMcoInterGesOpaSeq().intValue());
			if(lista.size() > 0 ){
				McoIntercorPasatus interPasatus = lista.get(0);
				
				if(interPasatus.getSeq() != null ){
					vo.setMcoInterPasatusSeq(interPasatus.getSeq());
				}
				if(interPasatus.getDescricao() != null) {
					vo.setMcoInterPasatusDescricao(interPasatus.getDescricao());
					vo.setDescricaoInterAtual(interPasatus.getDescricao());
				}
				if(interPasatus.getIndSituacao() != null) {
					vo.setMcoInterPasatusSituacao(interPasatus.getIndSituacao());
				}
				if(interPasatus.getMensagemAlerta() != null) {
					vo.setMcoInterPasatusMsgAlerta(interPasatus.getMensagemAlerta());
				}
				if(interPasatus.getAghCid() != null) {
					vo.setMcoInterPasatusCidSeq(interPasatus.getAghCid().getSeq());
				}
			}
		}
	}
	
	/**
	 * 
	 Retorno de CF_VIRGULAFormula.sql sendo:

	 :TBP_TAQUICARDIA de Q_ATEND_TRAB_PARTO
	 :TBP_SEM_ACELER_TRANS de Q_ATEND_TRAB_PARTO
	 :TBP_ANALGESIA_BPD de Q_ATEND_TRAB_PARTO
	 :TBP_ANALGESIA_BSD de Q_ATEND_TRAB_PARTO
	 :TBP_VAR_BATIDA_MENOR10 de Q_ATEND_TRAB_PARTO
	 :TBP_CARDIOTOCOGRAFIA de Q_ATEND_TRAB_PARTO
	  
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void obterFraseMonitoramentos(SumarioAtdRecemNascidoSlPartoVO vo) {
		Integer cardiotocografia = 0;
		Integer taquicardia = 0;
		Integer semAcelerTrans = 0;
		Integer analgesiaBpd = 0;
		Integer analgesiaBsd = 0;
		StringBuilder sbFrase = new StringBuilder();
		
		if (vo.getTaquicardia() != null) {
			cardiotocografia = 1;
		}
		
		if (vo.getSemAcelerarTrans() != null) {
			cardiotocografia = 1;
			taquicardia = 1;
		}
		
		if (vo.getAnalgesiaBpd() != null) {
			cardiotocografia = 1;
			taquicardia = 1;
			semAcelerTrans = 1;
		}
		
		if (vo.getAnalgesiaBsd() != null) {
			cardiotocografia = 1;
			taquicardia = 1;
			semAcelerTrans = 1;
			analgesiaBpd = 1;
		}
		
		if (vo.getVarBatidaMenor10() != null) {
			cardiotocografia = 1;
			taquicardia = 1;
			semAcelerTrans = 1;
			analgesiaBpd = 1;
			analgesiaBsd = 1;
		}
		
		if (vo.getCardiotocografia() != null) {
			if (cardiotocografia > 0) {
				sbFrase.append(vo.getCardiotocografia() ).append( ", ");
			} else {
				sbFrase.append(vo.getCardiotocografia() ).append(' ');
			}
		}
		
		if (vo.getTaquicardia() != null) {
			if (taquicardia > 0) {
				sbFrase.append(vo.getTaquicardia() ).append( ", ");
			} else {
				sbFrase.append(vo.getTaquicardia() ).append(' ');
			}
		}
		
		if (vo.getSemAcelerarTrans() != null) {
			if (semAcelerTrans > 0) {
				sbFrase.append(vo.getSemAcelerarTrans() ).append( ", ");
			} else {
				sbFrase.append(vo.getSemAcelerarTrans() ).append(' ');
			}
		}
		
		if (vo.getAnalgesiaBpd() != null) {
			if (analgesiaBpd > 0) {
				sbFrase.append(vo.getAnalgesiaBpd() ).append( ", ");
			} else {
				sbFrase.append(vo.getAnalgesiaBpd() ).append(' ');
			}
		}
		
		if (vo.getAnalgesiaBsd() != null) {
			if (analgesiaBsd > 0) {
				sbFrase.append(vo.getAnalgesiaBsd() ).append( ", ");
			} else {
				sbFrase.append(vo.getAnalgesiaBsd() ).append(' ');
			}
		}
		
		if (vo.getVarBatidaMenor10() != null) {
			sbFrase.append(vo.getVarBatidaMenor10());
		}
		
		vo.setMonitoramentos(StringUtils.trim(sbFrase.toString())); 
	}	
	
	/**
	 * 
	Retorno de CF_DURACAOFormula.sql sendo:


	:NAS_GSO_PAC_CODIGO 	de Q_NASCIMENTO
	:NAS_GSO_SEQP 			de Q_NASCIMENTO
	:PERIODO_DILATACAO 		de Q_NASCIMENTO
	:PERIODO_EXPULSIVO 		de Q_NASCIMENTO
	:NAS_DTHR_NASCIMENTO 	de Q_NASCIMENTO
	
	 * 
	 * @param vo
	 * @author daniel.silva
	 * @since 07/08/2012
	 */
	public String obterDuracaoDoParto(Integer nasGsoPacCodigo, Short nasGspSeqp,
			Short periodoDilatacao, Short periodoExpulsivo,
			Date nasDthrNascimento) {
	
		Date vDthrToque = null;
		String vDuracao = null;
		
		List<McoAtendTrabPartos> partos = getPerinatologiaFacade().listarAtendTrabPartos(nasGsoPacCodigo, nasGspSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		if (!partos.isEmpty()) {
			vDthrToque = partos.get(0).getDthrAtend();
		}
		
		if((periodoDilatacao != null && periodoDilatacao != 0) && (periodoExpulsivo != null && periodoExpulsivo != 0)){
			Short somaPeriodos = (short) (periodoDilatacao + periodoExpulsivo);
			vDuracao = formataTempoDecimal(somaPeriodos);
		}else{
			if(vDthrToque != null && nasDthrNascimento != null){
				vDuracao = getProntuarioOnlineFacade().diferencaEmHorasEMinutosFormatado(vDthrToque, nasDthrNascimento)+ " h";
			}
		}
		
		return vDuracao;
			
	}
	
	private String formataTempoDecimal(Short tempo) {
		String duracao = StringUtil.adicionaZerosAEsquerda(tempo, 4);
		StringBuilder hrDuracao = new StringBuilder();
		hrDuracao.append(duracao.substring(0, 2)).append(':')
				.append(duracao.substring(2, 4)).append('h');
		return hrDuracao.toString();
	}
	
	private IProntuarioOnlineFacade getProntuarioOnlineFacade(){
		return prontuarioOnlineFacade;
	}
	/**
	 * Estoria #15838
	 * 
	 * Q_OPA
	 * 
	 * Adicionada regra ao metodo preencherDadosIntercorrenciaGestacaoPassadas
	 * 
	 * @param vo
	 *//*
	private void preencherDadosIntercorrenciasPassadas(SumarioAtdRecemNascidoSlPartoVO vo) {
		if(vo != null && vo.getMcoInterGesOpaSeqPassada() != null) {
			McoIntercorPasatus obj = getPerinatologiaFacade().obterMcoIntercorPasatusPorChavePrimaria(vo.getMcoInterGesOpaSeqPassada().intValue());
			vo.setDescricaoInterPassada(obj.getDescricao());			
		}
	}*/
	
	/**
	 * Estoria #15838
	 * 
	 * Q_VEX
	 * 
	 * @param vo
	 */
	private void preencherDadosExames(SumarioAtdRecemNascidoSlPartoVO vo) {			
		if(vo != null) {
			List<SumarioAtdRecemNascidoSlPartoExamesMaeVO> examesMae = vo.getExamesMae();
			if(examesMae != null && !examesMae.isEmpty()){
				for(SumarioAtdRecemNascidoSlPartoExamesMaeVO exameMae : examesMae) {
					VMcoExames exame = getPerinatologiaFacade().obterVMcoExamesPorChave(exameMae.getRxsChave());
					if(exame != null) {
						exameMae.setDescricao(exame.getDescricao());
					}
				}
			}
		}
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return ambulatorioFacade;
	}
	
	/**
	 * CF_IDADE_PACFormula
	 * 
	 * @param dataNascimento
	 * @param recemNascido
	 * @return
	 * @author bruno.mourao
	 * @since 08/08/2012
	 */
	public String obterIdadeRecemNascido(Date dataNascimento, Date criadoEm){
		if(criadoEm != null){
			return getAmbulatorioFacade().obterIdadeMesDias(dataNascimento, criadoEm);
		}
		else{
			return "";
		}
	}
	
	/**
	 * Estoria #15838
	 * 
	 * Q4
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException  
	 */
	private void preencherDadosNotaAdicional(SumarioAtdRecemNascidoSlPartoVO vo) throws ApplicationBusinessException {
		
		if(vo.getRecemNascido() != null){
			
			List<McoNotaAdicional> notasAdicionais = getPerinatologiaFacade()
					.pesquisarMcoNotaAdicional(vo.getPacCodigo(),
							vo.getGsoSeqp(), null,
							DominioEventoNotaAdicional.MCOR_RN_SL_PARTO,
							McoNotaAdicional.Fields.CRIADO_EM);
			
			List<LinhaReportVO> listaNotaVo = null;
			for(McoNotaAdicional notaAdicional : notasAdicionais) {
				if(listaNotaVo == null){
					listaNotaVo = new ArrayList<LinhaReportVO>();
				}
				LinhaReportVO linha = new LinhaReportVO();
				linha.setTexto1(notaAdicional.getNotaAdicional());
				linha.setTexto2((DateUtil.obterDataFormatadaHoraMinutoSegundo(notaAdicional.getCriadoEm()).replace(" ", ", ").concat(" h")));
				linha.setTexto3((getRelExameFisicoRecemNascidoPOLON().formataNomeProf(notaAdicional.getSerMatricula(), notaAdicional.getSerVinCodigo())));
				listaNotaVo.add(linha);
			}
			
			vo.setListaNotasAdicionais(listaNotaVo);
		}
	}
	
		
	/**
	 * Estoria #15838
	 * 
	 * Q_PNI
	 * 
	 * @param vo
	 */
	private void preencherDadosReanimacao(SumarioAtdRecemNascidoSlPartoVO vo) {
		if(vo != null) {
			if(vo.getRecemNascido() != null){ 
				StringBuilder sBuilder = new StringBuilder();
				for(McoReanimacaoRns reanimacao: vo.getRecemNascido().getMcoReanimacaoRnses()){
					McoProcReanimacao procRea = getPerinatologiaFacade().obterMcoProcReanimacaoPorId(reanimacao.getId().getPniSeq());
					if(sBuilder.length()!=0){//!sBuilder.toString().equals("")
						sBuilder.append(", ");
					}
					sBuilder.append(procRea.getDescricao());
				}
				if(sBuilder.length()!=0) {//!sBuilder.toString().equals("")
					vo.setDescricaoPni(sBuilder.toString());
				}
			}
		}
	}
	/**
	 * Estoria #15838
	 * 
	 * Q1
	 * 
	 * @param vo
	 */
	private void preencherDadosNascIndicacoes(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		List<McoNascIndicacoes> lista = getPerinatologiaFacade().
									    listarNascIndicacoesPorCesariana(vo.getPacCodigo(),vo.getSeqp().intValue(), vo.getGsoSeqp());
		
		if(lista.size() > 0) {
			
			McoNascIndicacoes mcoNascIndicacoes = lista.get(0);
			
			if(mcoNascIndicacoes.getMcoCesarianas() != null){
			
				vo.setCesariana(mcoNascIndicacoes.getMcoCesarianas());
			}
			
			vo.setMcoNascIndNaiSeqp(mcoNascIndicacoes.getSeq());
			
			if (mcoNascIndicacoes.getIndicacaoNascimento() != null) {
				vo.setMcoNascIndNaiInaSeq(mcoNascIndicacoes.getIndicacaoNascimento().getSeq().toString());
			}
			
		}
		
	}
	
	/**
	 * Estoria #15838
	 * 
	 * Q_IND_NASC
	 * 
	 * @param vo
	 */
	private void preencherDadosIndicacaoNascimentos(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		if(vo.getMcoNascIndNaiInaSeq() != null){
			McoIndicacaoNascimento mcoIndicacaoNascimento = getPerinatologiaFacade().obterMcoIndicacaoNascimentoPorChavePrimaria(Integer.valueOf(vo.getMcoNascIndNaiInaSeq()));
			
			if(mcoIndicacaoNascimento != null){
				if (mcoIndicacaoNascimento.getSeq() != null) {
					vo.setNaiSeq(mcoIndicacaoNascimento.getSeq().toString());
				}
				if (mcoIndicacaoNascimento.getDescricao() != null) {	
					vo.setNaiDescIndForcipe(mcoIndicacaoNascimento.getDescricao());
				}
			}
		}
	}
	
	/**
	 * Estoria #15838
	 * 
	 * Q_ATD
	 * 
	 * @param vo
	 */
	private void preencherDadosAtendimentos(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		Integer pacCodRN = null;
		
		if (vo.getRecemNascido() != null
				&& vo.getRecemNascido().getPaciente().getCodigo() != null) {
			pacCodRN = vo.getRecemNascido().getPaciente().getCodigo();
		}
		
		if(pacCodRN != null){
			List<AghAtendimentos> lista = getAghuFacade().listarAtendimentosPorPacCodGestacaoSeq(
																			vo.getPacCodigo(), 
																			vo.getGsoSeqp(), 
																			pacCodRN); 
	
			if(lista.size() > 0) {
				
				AghAtendimentos aghAtendimentos = lista.get(0);
				
				if(aghAtendimentos.getSeq() !=  null) {
					vo.setAtdSeq(aghAtendimentos.getSeq());
				}
				if(aghAtendimentos.getIndPacAtendimento() != null) {
					vo.setAtdIndPac(aghAtendimentos.getIndPacAtendimento().getDescricao());
				}
			}
		}
	}

	/**
	 * Estoria #15838
	 * 
	 * Q_PROFISS_ENVOLVIDOS
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException  
	 */
	private void preencherDadosProfissionaisEnvolvidos(SumarioAtdRecemNascidoSlPartoVO vo) throws ApplicationBusinessException {
		if(vo != null) {
			
			//Obtém responsável e data
			List<McoLogImpressoes> logParto = getPerinatologiaFacade().pesquisarLogImpressoesEventoMcorRnSlParto(vo.getPacCodigo(), vo.getGsoSeqp());
			if(logParto != null && !logParto.isEmpty()){
				McoLogImpressoes logImpressoes = logParto.get(0);
				RapServidoresId servidoresId = logImpressoes.getServidor().getId();
				vo.setResponsavel(getSumarioAtendimentoRecemNascidoRN().obterCPResponsavel(servidoresId.getMatricula(),servidoresId.getVinCodigo()));
				vo.setDtHrMovimento(DateUtil.obterDataFormatadaHoraMinutoSegundo(logImpressoes.getCriadoEm()).replace(" ", ", ").concat(" h."));
				vo.setDtHrMovimentoOriginal(logImpressoes.getCriadoEm());
			}
			List<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO> profissionais = new ArrayList<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO>();
			McoNascimentos nascimento = vo.getNascimento();
			if(nascimento != null) {
				for(McoProfNascs obj : nascimento.getMcoProfNascses()) {
					SumarioAtdRecemNascidoProfissionaisEnvolvidosVO profissional = new SumarioAtdRecemNascidoProfissionaisEnvolvidosVO();
					String nome = getSumarioAtendimentoRecemNascidoRN().obterNomeProfissional(obj.getId().getSerMatriculaNasc(), obj.getId().getSerVinCodigoNasc());
					String conselho = getSumarioAtendimentoRecemNascidoRN().obterNomeNumeroConselho(obj.getId().getSerMatriculaNasc(), obj.getId().getSerVinCodigoNasc());
					profissional.setNomeProfissional(nome);
					profissional.setConselhoProfissional(conselho);
					profissionais.add(profissional);
				}
			}
			vo.setProfissionaisEnvolvidos(profissionais);
		}
	}
	
	/**
	 * Retorno de CF_VIRGULA_3.sql sendo:
	 * 
	 * RNA_EVACUOU de Q_RECEM_NASCIDOS
	 * RNA_SURFACTANTE de Q_RECEM_NASCIDOS
	 * RNA_LAVADO_GASTRICO de Q_RECEM_NASCIDOS
	 * RNA_AMAMENTADO de Q_RECEM_NASCIDOS
	 * RNA_URINOU de Q_RECEM_NASCIDOS
	 * 
	 * Estoria #15838
	 * 
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void preencherCampoInformacoesComplementaresRN(SumarioAtdRecemNascidoSlPartoVO vo) {
		
		StringBuilder retorno = new StringBuilder();
		
		int urinou = 0;
		int evacuou = 0;
		int surfactante = 0;
		int lavadoGastrico = 0;

		if(StringUtils.isNotBlank(vo.getEvacuou())) {
			urinou = 1;
		}
		if(StringUtils.isNotBlank(vo.getSurfactante())) {
			urinou = 1;
			evacuou = 1;
		}
		if(StringUtils.isNotBlank(vo.getLavadoGastrico())) {
			urinou = 1;
			evacuou = 1;
			surfactante = 1;
		}
		if(StringUtils.isNotBlank(vo.getAmamentado())) {
			urinou = 1;
			evacuou = 1;
			surfactante = 1;
			lavadoGastrico = 1;
		}

		// urinou
		if(StringUtils.isNotBlank(vo.getUrinou())) {
			if(urinou > 0) {
				retorno.append(vo.getUrinou()).append(", ");
			} else {
				retorno.append(vo.getUrinou()).append(' ');
			}
		}
		// evacuou
		if(StringUtils.isNotBlank(vo.getEvacuou())) {
			if(evacuou > 0) {
				retorno.append(vo.getEvacuou()).append(", ");
			} else {
				retorno.append(vo.getEvacuou()).append(' ');
			}
		}
		// surfactante
		if(StringUtils.isNotBlank(vo.getSurfactante())) {
			if(surfactante > 0) {
				retorno.append(vo.getSurfactante()).append(", ");
			} else {
				retorno.append(vo.getSurfactante()).append(' ');
			}
		}
		// lavado gastrico
		if(StringUtils.isNotBlank(vo.getLavadoGastrico())) {
			if(lavadoGastrico > 0) {
				retorno.append(vo.getLavadoGastrico()).append(", ");
			} else {
				retorno.append(vo.getLavadoGastrico()).append(' ');
			}
		}
		// campo 93 do relatorio
		if(StringUtils.isNotBlank(vo.getAmamentado())) {
			vo.setInformacoesComplementaresRN(StringUtils.trim(retorno.append(vo.getAmamentado()).toString()));
		} else {
			vo.setInformacoesComplementaresRN(StringUtils.trim(retorno.toString()));
		}
	}

	
	
	
	/**
	 * Estoria #15838
	 * CF_TESTE_CLASSFFormula
	 * @param vo
	 */
	private void obterClassificacaoMcoNascimento(SumarioAtdRecemNascidoSlPartoVO vo) {
		McoNascimentos nasc = vo.getNascimento();
		StringBuilder classificacao = new StringBuilder();
		if(nasc != null && nasc.getRnClassificacao() != null){
			if(nasc.getRnClassificacao().equals(DominioRNClassificacaoNascimento.NAV)){
				classificacao.append(DominioRNClassificacaoGestacao.NAV.getDescricao());
			}else if(nasc.getRnClassificacao().equals(DominioRNClassificacaoNascimento.NAM)){
				classificacao.append(DominioRNClassificacaoGestacao.NAM.getDescricao());
			}		
			vo.setClassificacaoRecemNascido(classificacao.toString());
		}
	}

	/**
	 * Estoria #15838
	 * CF_VIRGULA_2Formula
	 * @param vo
	 */
	private void obterReanimacao(SumarioAtdRecemNascidoSlPartoVO vo) {
		Boolean asp = Boolean.FALSE;		 
		Boolean aspTet = Boolean.FALSE;	
		Boolean inal = Boolean.FALSE; 
		Boolean ventMasc = Boolean.FALSE;   
	    //verifico se tem algum campo não nulo dos que vem a seguir dai devo usar virgula aspiracao asp

	    if(vo.getRecemNascido() != null){
		    //RNA_ASPIRACAO_TET
	    	if (vo.getRecemNascido().getIndAspiracaoTet() != null) {
	    		asp = Boolean.TRUE;	    		
	    	}	    	
		    //RNA_O2_INALATORIO
			if(vo.getRecemNascido().getIndO2Inalatorio() != null){
				asp = Boolean.TRUE; 
				aspTet = Boolean.TRUE;   	
			}
			//RNA_VENTILACAO_POR_MASCARA
			if(vo.getRecemNascido().getIndVentilacaoPorMascara() != null){
				asp = Boolean.TRUE;
				aspTet = Boolean.TRUE;
				inal = Boolean.TRUE;
			}
			//RNA_MASS_CARDIACA_EXT
			if(vo.getRecemNascido().getIndMassCardiacaExt() != null){
				asp = Boolean.TRUE;
				aspTet = Boolean.TRUE;
				inal = Boolean.TRUE;
				ventMasc = Boolean.TRUE;
			}
			
			montarReanimacao(vo, asp, aspTet, inal, ventMasc);
	    }
	}

	private void montarReanimacao(SumarioAtdRecemNascidoSlPartoVO vo, Boolean asp, Boolean aspTet, Boolean inal, Boolean ventMasc) {
		StringBuilder frase = new StringBuilder();
		//TESTE PARA VER SE O CAMPO ? NULO.
		//COLOCO A VIRGULA SE TEM MAIS ALGUM CAMPO PARA IMPRIMIR DEPOIS NA LINHA no layout model
		//1
		//RNA_ASPIRACAO
		if(vo.getAspiracao() != null){
			 if(asp){
		    	frase.append(vo.getAspiracao()).append(", ");
			 }else{
			 	frase.append(vo.getAspiracao());
			 }
		}    
		//RNA_ASPIRACAO_TET
		if (vo.getAspiracaoTet() != null) {
			 if(aspTet){
				 frase.append(vo.getAspiracaoTet()).append(", ");
			 }else{
				 frase.append(' ');
			 }
		}
		//RNA_O2_INALATORIO
		if (vo.getInalatorio() != null) {
			if(inal){
				frase.append(vo.getInalatorio()).append(", ");
			}else{
				frase.append(' ');
			}
		}
		//RNA_VENTILACAO_POR_MASCARA
		if (vo.getVentPorMascara() != null) {
			if(ventMasc){
				frase.append(vo.getVentPorMascara()).append(", ");
			}else{
				frase.append(' ');
			}
		}
		//RNA_MASS_CARDIACA_EXT
		if(StringUtils.isNotEmpty(vo.getMassaCardiacaExt())) {
			frase.append(vo.getMassaCardiacaExt());
		}
		vo.setReanimacao(StringUtils.removeEnd(frase.toString(), ", "));
	}	
	
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer gsoPacCodigo, Short gsoSeqp) {
		List<McoRecemNascidos> listaRecemNascidos = getPerinatologiaFacade().pesquisarMcoRecemNascidoPorGestacaoOrdenado(gsoPacCodigo, gsoSeqp);
		if (listaRecemNascidos != null && !listaRecemNascidos.isEmpty()) {
			return listaRecemNascidos;
		}
		return null;
	}	
	
	private void verificarExibicaoIdadeGestacao(SumarioAtdRecemNascidoSlPartoVO vo) {
		if (vo != null 
			&& vo.getGestacao() != null 
			&& vo.getGestacao().getDtInformadaIg() != null 
			&& vo.getDtHrMovimentoOriginal() != null) {
		
			if (vo.getGestacao().getDtInformadaIg().before(vo.getDtHrMovimentoOriginal())) {
				vo.setExibirIdadeGestacao(true);
			} else {
				vo.setExibirIdadeGestacao(false);
			}
		}
	}
}
