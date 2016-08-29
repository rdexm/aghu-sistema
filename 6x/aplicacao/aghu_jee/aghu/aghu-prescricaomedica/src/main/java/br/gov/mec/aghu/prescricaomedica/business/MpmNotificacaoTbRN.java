package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseEspecIdade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndEstreptomicina;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndEtambutol;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndEtionamida;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndIsoniazida;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndPirazinamida;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndRifampicina;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseRaca;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSexo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoNotificacao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.MpmNotificacaoTb;
import br.gov.mec.aghu.model.MpmNotificacaoTbJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotificacaoTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotificacaoTbJnDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AtendimentoJustificativaUsoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.GerarPDFSinanVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MpmNotificacaoTbRN extends BaseBusiness {

	private static final long serialVersionUID = 8226612349163188469L;

	private static final Log LOG = LogFactory.getLog(MpmNotificacaoTbRN.class);

	public enum MpmNotificacaoTbRNRNExceptionCode implements BusinessExceptionCode {
		ERRO_AO_INSERIR_JOURNAL, MPM_03722, MPM_03833;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MpmNotificacaoTbDAO mpmNotificacaoTbDAO;

	@Inject
	private MpmNotificacaoTbJnDAO mpmNotificacaoTbJnDAO;

	/**
	 * @ORADB TRIGGER MPMT_NTB_BRU BEFORE UPDATE
	 * @param ntb
	 */
	private void preAtualizar(MpmNotificacaoTb ntb) {
		ntb.setServidorAlterado(this.servidorLogadoFacade.obterServidorLogado());
	}

	/**
	 * @ORADB TRIGGER MPMT_NTB_ARU AFTER UPDATE
	 * @param ntb
	 * @param original
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizar(MpmNotificacaoTb ntb, MpmNotificacaoTb original) throws ApplicationBusinessException {

		/*
		 * As condicionais foram divididas para evitar complexidade
		 */

		final boolean modificados1 = AGHUUtil.modificados(ntb.getHiv(), original.getHiv()) || AGHUUtil.modificados(ntb.getHistopatologia(), original.getHistopatologia()) || AGHUUtil.modificados(ntb.getDtInicioTratAtual(), original.getDtInicioTratAtual()) || AGHUUtil.modificados(ntb.getTratSupervisionado(), original.getTratSupervisionado()) || AGHUUtil.modificados(ntb.getIndRifampicina(), original.getIndRifampicina()) || AGHUUtil.modificados(ntb.getIndEtambutol(), original.getIndEtambutol()) || AGHUUtil.modificados(ntb.getDescCulturaOutroMaterial(), original.getDescCulturaOutroMaterial()) || AGHUUtil.modificados(ntb.getDescOutroAgravo(), original.getDescOutroAgravo()) || AGHUUtil.modificados(ntb.getBaciloscopiaEscarro(), original.getBaciloscopiaEscarro()) || AGHUUtil.modificados(ntb.getBaciloscopiaOutroMaterial(), original.getBaciloscopiaOutroMaterial()) || AGHUUtil.modificados(ntb.getDescBaciloOutroMaterial(), original.getDescBaciloOutroMaterial())
				|| AGHUUtil.modificados(ntb.getCulturaEscarro(), original.getCulturaEscarro()) || AGHUUtil.modificados(ntb.getCulturaOutroMaterial(), original.getCulturaOutroMaterial()) || AGHUUtil.modificados(ntb.getAgravoAssociado(), original.getAgravoAssociado()) || AGHUUtil.modificados(ntb.getIndOssea(), original.getIndOssea()) || AGHUUtil.modificados(ntb.getIndOcular(), original.getIndOcular()) || AGHUUtil.modificados(ntb.getIndMiliar(), original.getIndMiliar()) || AGHUUtil.modificados(ntb.getIndMeningite(), original.getIndMeningite()) || AGHUUtil.modificados(ntb.getIndOutraExtrapulmonar(), original.getIndOutraExtrapulmonar()) || AGHUUtil.modificados(ntb.getIndNaoExtrapulmonar(), original.getIndNaoExtrapulmonar()) || AGHUUtil.modificados(ntb.getDescrOutraExtrapulmonar(), original.getDescrOutraExtrapulmonar()) || AGHUUtil.modificados(ntb.getSexo(), original.getSexo()) || AGHUUtil.modificados(ntb.getRaca(), original.getRaca())
				|| AGHUUtil.modificados(ntb.getEscolaridade(), original.getEscolaridade()) || AGHUUtil.modificados(ntb.getNroCartaoSus(), original.getNroCartaoSus()) || AGHUUtil.modificados(ntb.getNomeMae(), original.getNomeMae()) || AGHUUtil.modificados(ntb.getLogradouro(), original.getLogradouro()) || AGHUUtil.modificados(ntb.getNumeroLogradouro(), original.getNumeroLogradouro()) || AGHUUtil.modificados(ntb.getComplLogradouro(), original.getComplLogradouro()) || AGHUUtil.modificados(ntb.getPontoReferencia(), original.getPontoReferencia()) || AGHUUtil.modificados(ntb.getMunicipioResidencia(), original.getMunicipioResidencia()) || AGHUUtil.modificados(ntb.getDistrito(), original.getDistrito()) || AGHUUtil.modificados(ntb.getEspecIdade(), original.getEspecIdade()) || AGHUUtil.modificados(ntb.getCep(), original.getCep()) || AGHUUtil.modificados(ntb.getDddTelefone(), original.getDddTelefone()) || AGHUUtil.modificados(ntb.getNumeroTelefone(), original.getNumeroTelefone())
				|| AGHUUtil.modificados(ntb.getZona(), original.getZona()) || AGHUUtil.modificados(ntb.getPais(), original.getPais()) || AGHUUtil.modificados(ntb.getAtendimento(), original.getAtendimento()) || AGHUUtil.modificados(ntb.getServidor(), original.getServidor()) || AGHUUtil.modificados(ntb.getServidorAlterado(), original.getServidorAlterado()) || AGHUUtil.modificados(ntb.getBairro(), original.getBairro()) || AGHUUtil.modificados(ntb.getServidorValidado(), original.getServidorValidado()) || AGHUUtil.modificados(ntb.getBairroCepLogradouro(), original.getBairroCepLogradouro()) || AGHUUtil.modificados(ntb.getCidade(), original.getCidade()) || AGHUUtil.modificados(ntb.getUf(), original.getUf()) || AGHUUtil.modificados(ntb.getIndIsoniazida(), original.getIndIsoniazida()) || AGHUUtil.modificados(ntb.getIndEstreptomicina(), original.getIndEstreptomicina()) || AGHUUtil.modificados(ntb.getIndPirazinamida(), original.getIndPirazinamida());

		final boolean modificados2 = AGHUUtil.modificados(ntb.getIndEtionamida(), original.getIndEtionamida()) || AGHUUtil.modificados(ntb.getOutrasDrogas(), original.getOutrasDrogas()) || AGHUUtil.modificados(ntb.getIndDoencaRelTrabalho(), original.getIndDoencaRelTrabalho()) || AGHUUtil.modificados(ntb.getMunicipioInvestigador(), original.getMunicipioInvestigador()) || AGHUUtil.modificados(ntb.getProntuario(), original.getProntuario()) || AGHUUtil.modificados(ntb.getNomePaciente(), original.getNomePaciente()) || AGHUUtil.modificados(ntb.getDtNascimento(), original.getDtNascimento()) || AGHUUtil.modificados(ntb.getIdade(), original.getIdade()) || AGHUUtil.modificados(ntb.getDtNotificacao(), original.getDtNotificacao()) || AGHUUtil.modificados(ntb.getMunicipioNotificacao(), original.getMunicipioNotificacao()) || AGHUUtil.modificados(ntb.getUnidadeDeSaude(), original.getUnidadeDeSaude()) || AGHUUtil.modificados(ntb.getDtDiagnostico(), original.getDtDiagnostico())
				|| AGHUUtil.modificados(ntb.getTipoEntrada(), original.getTipoEntrada()) || AGHUUtil.modificados(ntb.getRaioxTorax(), original.getRaioxTorax()) || AGHUUtil.modificados(ntb.getTesteTuberculinico(), original.getTesteTuberculinico()) || AGHUUtil.modificados(ntb.getForma(), original.getTesteTuberculinico()) || AGHUUtil.modificados(ntb.getIndPleural(), original.getIndPleural()) || AGHUUtil.modificados(ntb.getIndGangPerif(), original.getIndGangPerif()) || AGHUUtil.modificados(ntb.getIndGenitoUrinaria(), original.getIndGenitoUrinaria()) || AGHUUtil.modificados(ntb.getTipoNotificacao(), original.getTipoNotificacao()) || AGHUUtil.modificados(ntb.getSeq(), original.getSeq()) || AGHUUtil.modificados(ntb.getCriadoEm(), original.getCriadoEm()) || AGHUUtil.modificados(ntb.getIndConcluido(), original.getIndConcluido()) || AGHUUtil.modificados(ntb.getIndGestante(), original.getIndGestante()) || AGHUUtil.modificados(ntb.getGeoCampo1(), original.getGeoCampo1())
				|| AGHUUtil.modificados(ntb.getGeoCampo2(), original.getGeoCampo2()) || AGHUUtil.modificados(ntb.getIndInstitucionalizado(), original.getIndInstitucionalizado()) || AGHUUtil.modificados(ntb.getBaciloscopiaEscarro2(), original.getBaciloscopiaEscarro2()) || AGHUUtil.modificados(ntb.getIndAids(), original.getIndAids()) || AGHUUtil.modificados(ntb.getIndAlcoolismo(), original.getIndAlcoolismo()) || AGHUUtil.modificados(ntb.getIndDiabetes(), original.getIndDiabetes()) || AGHUUtil.modificados(ntb.getIndDoencaMental(), original.getIndDoencaMental()) || AGHUUtil.modificados(ntb.getIndCutanea(), original.getIndCutanea()) || AGHUUtil.modificados(ntb.getIndLaringea(), original.getIndLaringea()) || AGHUUtil.modificados(ntb.getContatosRegistrados(), original.getContatosRegistrados());

		if (modificados1 || modificados2) {
			this.inserirJournal(ntb, original);
		}
	}

	private void inserirJournal(MpmNotificacaoTb ntb, MpmNotificacaoTb original) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		String user = servidorLogado != null ? servidorLogado.getUsuario() : null;

		MpmNotificacaoTbJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MpmNotificacaoTbJn.class, user);
		
		jn.setSerMatricula(ntb.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(ntb.getServidor().getId().getVinCodigo());
		
		if (ntb.getServidorAlterado() != null) {
			jn.setSerMatriculaAlterada(ntb.getServidorAlterado().getId().getMatricula());
			jn.setSerVinCodigoAlterada(ntb.getServidorAlterado().getId().getVinCodigo());
		}
		
		if (ntb.getServidorValidado() != null) {
			jn.setSerMatriculaValidada(ntb.getServidorValidado().getId().getMatricula());
			jn.setSerVinCodigoValidada(ntb.getServidorValidado().getId().getVinCodigo());	
		}
		
		try {
			PropertyUtils.copyProperties(jn, ntb);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ApplicationBusinessException(MpmNotificacaoTbRNRNExceptionCode.ERRO_AO_INSERIR_JOURNAL);
		}

		this.mpmNotificacaoTbJnDAO.persistir(jn);
	}
	
	/**
	 * 
	 * @param ntb
	 * @throws ApplicationBusinessException
	 */
	public void persistir(final MpmNotificacaoTb ntb) throws ApplicationBusinessException {
		MpmNotificacaoTb original = this.mpmNotificacaoTbDAO.obterOriginal(ntb);
		if (original == null) { // Inserir
			preInserir(ntb);
			this.mpmNotificacaoTbDAO.persistir(ntb);
		} else { // Atualizar
			preAtualizar(ntb);
			this.mpmNotificacaoTbDAO.merge(ntb);
			posAtualizar(ntb, original);
		}
	}
	
	//@ORADB MPM_NOTIFICACAO_TBS.MPMT_NTB_BRI
	public void preInserir(MpmNotificacaoTb ntb) {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		ntb.setCriadoEm(new Date());
		ntb.setServidor(servidorLogado);
	}

	/**
	 * Realiza as validações e a consulta para geração de PDF SINAN.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Dados a serem exibidos no relatório
	 * @throws ApplicationBusinessException
	 */
	public GerarPDFSinanVO gerarPdfSinan(Integer atdSeq) throws ApplicationBusinessException {

		RapServidores usuario = servidorLogadoFacade.obterServidorLogado();

		// @ORADB CGFK$QRY_JUM_MPM_JUM_VSCG_FK1
		if (registroColaboradorFacade.obterServidorConselhoGeralPorIdServidor(usuario.getId().getMatricula(), usuario.getId().getVinCodigo()) == null) {
			throw new ApplicationBusinessException(MpmNotificacaoTbRNRNExceptionCode.MPM_03722);
		}

		// CGFK$QRY_JUM_MPM_JUM_ATD_FK1
		aghuFacade.obterAtendimentoJustificativaUsoPorId(atdSeq);

		MpmNotificacaoTb notificacao = mpmNotificacaoTbDAO.obterNotificacaoImpressaoPorAtendimento(atdSeq);

		if (notificacao == null) {
			throw new ApplicationBusinessException(MpmNotificacaoTbRNRNExceptionCode.MPM_03833);
		}

		GerarPDFSinanVO retorno = mpmNotificacaoTbDAO.obterInformacoesPdfSinan(notificacao.getSeq());

		retorno.setUfSede(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_UF_SEDE_HU));
		if(retorno.getCep() != null && retorno.getCep() > 0){
			retorno.setCepFormatado(CoreUtil.formataCEP(retorno.getCep()));
		}
				
		return retorno;
	}

	/**
	 * @ORADB MPMP_VER_DADOS_ITENS
	 * 
	 * @param atdSeq
	 * @return seq_notificacao_tb
	 * @throws ApplicationBusinessException
	 */
	public Integer preInserirJustificativaUsoMedicamentosTb(Integer atdSeq) throws ApplicationBusinessException {
		
		AghParametros pCidadePadrao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CIDADE_PADRAO);
		AghParametros pHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_CLINICAS);
		AghParametros pCnes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CNES_HCPA);
		AghParametros pCid = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_TB_CID);
		AghParametros pDoenca = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_TB);
		
		MpmNotificacaoTb notificacaoTb = new MpmNotificacaoTb();
		
		List<AtendimentoJustificativaUsoMedicamentoVO> listaAtendimentos = pacienteFacade.obterPacientePorAtendimentoComEndereco(atdSeq);
		
		if (listaAtendimentos != null && !listaAtendimentos.isEmpty()) {
			AtendimentoJustificativaUsoMedicamentoVO cAtend = listaAtendimentos.get(0);
			
			this.converterSexoRacaPacienteJustificativaUsoMdtos(notificacaoTb, cAtend);
			
			if (cAtend.getBclCloCep() != null) {
				
				AipBairrosCepLogradouroId id = new AipBairrosCepLogradouroId(cAtend.getBclCloLgrCodigo(), 
						cAtend.getBclCloCep(), cAtend.getBclBaiCodigo());
				AipBairrosCepLogradouro cLograd = cadastroPacienteFacade.obterAipBairrosCepLogradouroPorId(id);
				
				notificacaoTb.setCep(cLograd.getId().getCloCep());
				notificacaoTb.setLogradouro(cLograd.getAipLogradouro().getNome());
				notificacaoTb.setBairro(cLograd.getAipBairro().getDescricao());
				notificacaoTb.setMunicipioResidencia(cLograd.getAipLogradouro().getAipCidade().getNome());
				notificacaoTb.setUf(cLograd.getAipLogradouro().getAipCidade().getAipUf());
				notificacaoTb.setCidade(cLograd.getAipLogradouro().getAipCidade());		
			} else {
				notificacaoTb.setCep(cAtend.getCep());
				notificacaoTb.setLogradouro(cAtend.getLogradouro());
				notificacaoTb.setBairro(cAtend.getBairro());
				notificacaoTb.setMunicipioResidencia(cAtend.getCidade());
				
				if (cAtend.getUfSigla() != null) {
					notificacaoTb.setUf(cadastrosBasicosPacienteFacade.obterUF(cAtend.getUfSigla()));
				}
				
				if (cAtend.getCddCodigo() != null) {
					notificacaoTb.setCidade(cadastrosBasicosPacienteFacade.obterCidadePorCodigo(cAtend.getCddCodigo()));
				}
			}
			
			this.informarIdadePacienteJustificativaUsoMdtos(notificacaoTb, cAtend);
			
			notificacaoTb.setTipoNotificacao(DominioNotificacaoTuberculoseTipoNotificacao.INDIVIDUAL);
			notificacaoTb.setDtNotificacao(new Date());
			notificacaoTb.setMunicipioNotificacao(pCidadePadrao.getVlrTexto());
			notificacaoTb.setUnidadeDeSaude(pHospital.getVlrTexto());
			notificacaoTb.setIndRifampicina(DominioNotificacaoTuberculoseIndRifampicina.POSITIVO_2);
			notificacaoTb.setIndEtambutol(DominioNotificacaoTuberculoseIndEtambutol.POSITIVO_2);
			notificacaoTb.setIndIsoniazida(DominioNotificacaoTuberculoseIndIsoniazida.NAO);
			notificacaoTb.setIndEstreptomicina(DominioNotificacaoTuberculoseIndEstreptomicina.POSITIVO_2);
			notificacaoTb.setIndPirazinamida(DominioNotificacaoTuberculoseIndPirazinamida.POSITIVO_2);
			notificacaoTb.setIndEtionamida(DominioNotificacaoTuberculoseIndEtionamida.POSITIVO_2);
			notificacaoTb.setMunicipioInvestigador(pCidadePadrao.getVlrTexto());
			notificacaoTb.setUnidSaudeInvestigador(pHospital.getVlrTexto());
			notificacaoTb.setCnes(pCnes.getVlrNumerico().intValue());
			notificacaoTb.setProntuario(cAtend.getProntuario());
			notificacaoTb.setNomePaciente(cAtend.getNome());
			notificacaoTb.setDtNascimento(cAtend.getDtNascimento());
			if (cAtend.getNroCartaoSaude() != null) {
				notificacaoTb.setNroCartaoSus(cAtend.getNroCartaoSaude().longValue());
			}
			notificacaoTb.setNomeMae(cAtend.getNomeMae());
			if (cAtend.getNroLogradouro() != null) {
				notificacaoTb.setNumeroLogradouro(cAtend.getNroLogradouro().toString());
			}
			notificacaoTb.setComplLogradouro(cAtend.getComplLogradouro());
			notificacaoTb.setDddTelefone(cAtend.getDddFoneResidencial());
			notificacaoTb.setNumeroTelefone(cAtend.getFoneResidencial());
			notificacaoTb.setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(cAtend.getAtdSeq()));
			if (cAtend.getBclCloCep() != null && cAtend.getBclBaiCodigo() 
					!= null && cAtend.getBclCloLgrCodigo() != null) {
				notificacaoTb.setBairroCepLogradouro(cadastroPacienteFacade.obterBairroCepLogradouroPorCepBairroLogradouro(
						cAtend.getBclCloCep(), cAtend.getBclBaiCodigo(), cAtend.getBclCloLgrCodigo()));
			}
			notificacaoTb.setIndConcluido(Boolean.FALSE);
			
			if(cAtend.getOcpCodigo() != null){
				notificacaoTb.setOcupacao(cadastrosBasicosPacienteFacade.obterAipOcupacoesPorChavePrimaria(cAtend.getOcpCodigo()));	
			}
			
			notificacaoTb.setIndImpresso(Boolean.FALSE);
			
			AghCid cid = this.aghuFacade.obterCid(pCid.getVlrTexto());
			notificacaoTb.setCid(cid);
		
			notificacaoTb.setDoenca(pDoenca.getVlrTexto());
			
			this.persistir(notificacaoTb);
			this.mpmNotificacaoTbDAO.flush();
		}

		List<MpmNotificacaoTb> cNotifTb = mpmNotificacaoTbDAO.pesquisarMpmNotificacaoTbPorAtendimento(atdSeq);
		
		if (cNotifTb != null) {
			return cNotifTb.get(0).getSeq();
		} else {
			return null;		
		}
	}
	
	private void informarIdadePacienteJustificativaUsoMdtos(MpmNotificacaoTb notificacaoTb,	AtendimentoJustificativaUsoMedicamentoVO cAtend) {
		Integer anos = DateUtil.getIdade(cAtend.getDtNascimento());
		Integer meses = DateUtil.getIdadeMeses(cAtend.getDtNascimento());
		Integer dias = DateUtil.getIdadeDias(cAtend.getDtNascimento());
		Period period = new Period(cAtend.getDtNascimento().getTime(), Calendar
				.getInstance().getTimeInMillis(), PeriodType.hours());
		Integer horas = period.getHours();
		
		if (anos == 0 && meses == 0 && dias == 0 && horas > 0) {
			notificacaoTb.setEspecIdade(DominioNotificacaoTuberculoseEspecIdade.HORA);
			notificacaoTb.setIdade(horas.shortValue());
		}			
		if (anos == 0 && meses == 0 && dias > 0) {
			notificacaoTb.setEspecIdade(DominioNotificacaoTuberculoseEspecIdade.DIA);
			notificacaoTb.setIdade(dias.shortValue());
		}			
		if (anos == 0 && meses > 0) {
			notificacaoTb.setEspecIdade(DominioNotificacaoTuberculoseEspecIdade.MÊS);
			notificacaoTb.setIdade(meses.shortValue());
		}			
		if (anos > 0) {
			notificacaoTb.setEspecIdade(DominioNotificacaoTuberculoseEspecIdade.ANO);
			notificacaoTb.setIdade(anos.shortValue());
		}
	}
	
	private void converterSexoRacaPacienteJustificativaUsoMdtos(MpmNotificacaoTb notificacaoTb,	AtendimentoJustificativaUsoMedicamentoVO cAtend) {
		if (DominioSexo.M.equals(cAtend.getSexo())) {
			notificacaoTb.setSexo(DominioNotificacaoTuberculoseSexo.M);
		} else if (DominioSexo.F.equals(cAtend.getSexo())) {
			notificacaoTb.setSexo(DominioNotificacaoTuberculoseSexo.F);
		}
		else if (DominioSexo.I.equals(cAtend.getSexo())) {
			notificacaoTb.setSexo(DominioNotificacaoTuberculoseSexo.I);
		}
		
		
		if (DominioCor.B.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.BRANCA);
		} else 	if (DominioCor.P.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.PRETA);
		} else 	if (DominioCor.A.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.AMARELA);
		} else 	if (DominioCor.I.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.INDIGENA);
		} else 	if (DominioCor.M.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.PARDA);
		} else 	if (DominioCor.O.equals(cAtend.getCor())) {
			notificacaoTb.setRaca(DominioNotificacaoTuberculoseRaca.IGNORADA);
		}
		
	}	
	
}
