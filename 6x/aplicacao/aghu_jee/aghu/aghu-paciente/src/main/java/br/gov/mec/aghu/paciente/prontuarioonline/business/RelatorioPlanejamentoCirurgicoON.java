package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioPlanejamentoCirurgicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioPlanejamentoCirurgicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICascaFacade cascaFacade;

@EJB
private IBancoDeSangueFacade bancoDeSangueFacade;

@EJB
private IProntuarioOnlineFacade prontuarioOnlineFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

@EJB
private ICertificacaoDigitalFacade certificacaoDigitalFacade;

@EJB
private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

@EJB
private IPacienteFacade pacienteFacade;

	private static final long serialVersionUID = 2940417922970811978L;

	public List<PlanejamentoCirurgicoVO> pesquisarRelatorioPlanejamentoCirurgico(
			Integer seqMbcAgenda, Integer seqMbcCirurgia)
			throws ApplicationBusinessException {

		List<PlanejamentoCirurgicoVO> listaRel = null;
		if(seqMbcCirurgia != null){
			MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(seqMbcCirurgia);
			MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade().retornaResponsavelCirurgia(cirurgia);
			
			listaRel = getBlocoCirurgicoFacade().pesquisarRelatorioPlanejamentoCirurgico(
					cirurgia.getData(), cirurgia.getEspecialidade().getSeq(),
							cirurgia.getPaciente().getCodigo(),
							profCirurgia.getId().getPucSerMatricula(),
							profCirurgia.getId().getPucSerVinCodigo(),
							profCirurgia.getId().getPucUnfSeq(),
							profCirurgia.getId().getPucIndFuncaoProf());
			
			
		}else if(seqMbcAgenda != null){
			MbcAgendas agendas = getBlocoCirurgicoFacade().obterAgendaPorChavePrimaria(seqMbcAgenda);
			
			listaRel = getBlocoCirurgicoFacade().pesquisarRelatorioPlanejamentoCirurgico(
					agendas.getDtAgenda(), agendas.getEspecialidade().getSeq(),
					agendas.getPaciente().getCodigo(),
							agendas.getProfAtuaUnidCirgs().getId().getSerMatricula(),
							agendas.getProfAtuaUnidCirgs().getId().getSerVinCodigo(),
							agendas.getProfAtuaUnidCirgs().getId().getUnfSeq(),
							agendas.getProfAtuaUnidCirgs().getId().getIndFuncaoProf());
		}
		
		processarRelatorioPlanejamentoCirurgico(listaRel);
		
		ordenarRelatorio(listaRel);
		
		return listaRel;

	}

	private void ordenarRelatorio(List<PlanejamentoCirurgicoVO> listaRel) {
		//ORDER BY 2 ASC, 14 ASC, 4 ASC, 11 ASC, 6 ASC, 12 ASC 
		
		/*AGD.DT_AGENDA  dt_agenda,
		SUBSTR(TO_CHAR(pac.prontuario,'FM99999999'),1,7)||'/'|| SUBSTR(TO_CHAR(pac.prontuario,'FM00000009'),8,1) prontuario,
		RAPC_BUSCA_NOME(agd.puc_ser_vin_codigo, agd.PUC_SER_MATRICULA) equipe,
		pac.nome nome_paciente,
		esp.nome_especialidade especialidade,
		MBCC_IDA_ANO_MES_DIA(pac.codigo) idade_pac,  -- FUNÇÃO JÁ MIGRADA*/
		
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.IDADE_PAC.toString(), 		Boolean.FALSE);
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.ESPECIALIDADE.toString(), 	Boolean.FALSE);
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.NOME_PACIENTE.toString(), 	Boolean.FALSE);
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.EQUIPE.toString(), 			Boolean.FALSE);
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.PRONTUARIO_INTEGER.toString(), Boolean.FALSE);
		CoreUtil.ordenarLista(listaRel, PlanejamentoCirurgicoVO.Fields.DT_AGENDA.toString(), 		Boolean.TRUE);
		      
	}

	private void processarRelatorioPlanejamentoCirurgico(
			List<PlanejamentoCirurgicoVO> listaRel) {
		for(PlanejamentoCirurgicoVO vo : listaRel){
			
			vo = popularPlanejamentoCirurgicoVO(vo);
			
			if(vo.getAlteradoEmMbcAgenda()== null){
				vo.setDtRegistro(DateUtil.dataToString(vo.getDtHrInclusao(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			}else{
				vo.setDtRegistro(DateUtil.dataToString(vo.getAlteradoEmMbcAgenda(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			}
			
			vo.setRespAgd(processarRespAgsPlanejCirurgico(vo));
			vo.setIdadePac(getProntuarioOnlineFacade().mbccIdaAnoMesDia(vo.getPacCodigo()));
			if(vo.getProntuarioInteger()!=null){
				vo.setProntuario(CoreUtil.formataProntuario(vo.getProntuarioInteger().toString()));
			}
			vo.setProcedimento(getAmbulatorioFacade().obterDescricaoCidCapitalizada(vo.getProcedimento(), CapitalizeEnum.PRIMEIRA));
			vo.setLocal(getProntuarioOnlineFacade().obterLocalizacaoPacienteParaRelatorio(vo.getLtoLtoId(), vo.getQrtNumero(), vo.getUnfSeq()));
			vo.setRegime(getRegimeMbcAgenda(vo.getRegimeDominio().getCodigo()));
			vo.setLadoCirugia(vo.getLadoCirugiaDominio() != null ? vo.getLadoCirugiaDominio().getDescricaoDefaultNA() : "Não se aplica");
			vo.setUnidadeFuncional(vo.getUnidadeFuncional().toUpperCase());
			if(vo.getCidCodigo() != null){
				vo.setDiagnostico(vo.getCidCodigo() + " - " + getAmbulatorioFacade().obterDescricaoCidCapitalizada(vo.getCidDescricao()));
			}
			vo.setvSysdate(DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm"));
			
			vo.setHemoterapicosList(pesquisarComponentesSanguineosAgendas(vo.getAgdSeq()));
		}
		
	}

	private PlanejamentoCirurgicoVO popularPlanejamentoCirurgicoVO(PlanejamentoCirurgicoVO vo){
		vo.setDtAgenda(DateUtil.dataToString(vo.getDtAgendaDate(),"dd/MM/yyyy"));
		vo.setTempoSala(DateUtil.dataToString(vo.getTempoSalaDate(), "HH:mm"));
		if(vo.getSexoPacDominio() != null){
			vo.setSexoPac(vo.getSexoPacDominio().getDescricao());
		}
		vo.setQtdeHemoAdic(vo.getQtdeHemoAdicShort() != null ? vo.getQtdeHemoAdicShort().intValue() : null);
		vo.setQtdeHemo(vo.getQtdeHemoShort() != null ? vo.getQtdeHemoShort().intValue() : null);
		vo.setEquipe(getProntuarioOnlineFacade().obterNomeProfissional(vo.getPucSerMatricula(), vo.getPucSerVinCodigo()));
		vo.setCrmEquipe(getPesquisaInternacaoFacade().buscarNroRegistroConselho(vo.getPucSerVinCodigo(), vo.getPucSerMatricula()));
		return vo;
	}
	
	private String getRegimeMbcAgenda(String regime) {
		if("A".equals(regime)){
			return "Ambulatório";
		}else if("I".equals(regime)){
			return "Internação";
		}else if("H".equals(regime)){
			return "Hospital Dia";
		}else if("9".equals(regime)){
			return "Internação até 72h";
		}else {
			return "xx";
		}
	}

	private String processarRespAgsPlanejCirurgico(PlanejamentoCirurgicoVO vo) {
		String nroConse = getRegistroColaboradorFacade().buscarNroRegistroConselho(
				vo.getSerVinCodigoAlteradoPor(),
				vo.getSerMatriculaAlteradoPor(), Boolean.TRUE);
		
		StringBuffer respAgd = new StringBuffer();
		
		if(nroConse == null){
			nroConse = getRegistroColaboradorFacade().buscarNroRegistroConselho(
					vo.getSerVinCodigo(),
					vo.getSerMatricula(), Boolean.TRUE);
			if(nroConse == null){
				respAgd.append("Dr(a). ");
				
				respAgd.append(getProntuarioOnlineFacade().obterNomeProfissional(vo.getPucSerMatricula(), vo.getPucSerVinCodigo()));
				
				respAgd.append("  CRM ");
				
				nroConse = getRegistroColaboradorFacade().buscarNroRegistroConselho(
						vo.getPucSerVinCodigo(),
						vo.getPucSerMatricula(), Boolean.TRUE);
				respAgd.append(nroConse == null ? "" : nroConse);
			}else{
				respAgd.append("Dr(a). ");
				
				respAgd.append(getProntuarioOnlineFacade().obterNomeProfissional(vo.getSerMatricula(), vo.getSerVinCodigo()));
				
				respAgd.append("  CRM ");
				
				nroConse = getRegistroColaboradorFacade().buscarNroRegistroConselho(
						vo.getSerVinCodigo(),
						vo.getSerMatricula(), Boolean.TRUE);
				respAgd.append(nroConse == null ? "" : nroConse);				
			}			
		}else{
			respAgd.append("Dr(a). ");
			
			respAgd.append(getProntuarioOnlineFacade().obterNomeProfissional(vo.getSerMatriculaAlteradoPor(), vo.getSerVinCodigoAlteradoPor()));
			
			respAgd.append("  CRM ");
			
			nroConse = getRegistroColaboradorFacade().buscarNroRegistroConselho(
					vo.getSerVinCodigoAlteradoPor(),
					vo.getSerMatriculaAlteradoPor(), Boolean.TRUE);
			respAgd.append(nroConse == null ? "" : nroConse);			
		}
		
		return respAgd.toString();
	}

	public MbcAgendas obtemAgendaPlanejamentoCirurgico(Integer seqCirurgia) throws ApplicationBusinessException {
		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(seqCirurgia);
		if(cirurgia == null){
			return null;
		}
		
		MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade().retornaResponsavelCirurgia(cirurgia);
		
		List<MbcAgendas> agendas = getBlocoCirurgicoFacade().pesquisarCirurgiasAgendadasPorResponsavel(cirurgia, profCirurgia);

		
		if(agendas != null && !agendas.isEmpty()){
			// O teste abaixo equivale ao teste "ASSINATURA DIGITAL" que havia no AGH 
			MbcAgendas agenda = agendas.get(0);
			if (agenda.getServidor().getUsuario() == null || !getCascaFacade().usuarioTemPermissao(agenda.getServidor().getUsuario(), "assinaturaDigital", "visualizarRelatorioPlanejamentoCirurgico")) {
				return null;
			}
			return agenda;
		}
		
		return null;
	}
	
	public Boolean verificarSeDocumentoPlanejamentoCirugicoAssinado(
			Integer seqCirurgia) throws ApplicationBusinessException {
		DominioTipoDocumento tipo = DominioTipoDocumento.PC;
		List<DominioSituacaoVersaoDocumento> situacoes = Arrays.asList(DominioSituacaoVersaoDocumento.P, DominioSituacaoVersaoDocumento.A);
		
		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(seqCirurgia);
		
		MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade().retornaResponsavelCirurgia(cirurgia);
		
		List<MbcAgendas> agenda = getBlocoCirurgicoFacade()
		.pesquisarCirurgiasAgendadasPorResponsavel(cirurgia, profCirurgia);
		
		Boolean certifAssDoc = verificarCertificadoAssinado(agenda.get(0).getSeq(), tipo, situacoes);

		return certifAssDoc;
	}
	
	
	public Boolean verificarCertificadoAssinado(Integer seqAgenda, DominioTipoDocumento tipo, List<DominioSituacaoVersaoDocumento> situacoes) {
		List<AghVersaoDocumento> versaoDocumentos = getCertificacaoDigitalFacade().verificaImprime(seqAgenda, tipo, situacoes);
		if(versaoDocumentos != null && versaoDocumentos.size() > 0){
			return true;
		}else{
			return false;
		}
	}
		
	public Integer chamarDocCertifPlanejamentoCirurgico(Integer seqCirurgia){
		
		DominioTipoDocumento tipo = DominioTipoDocumento.PC;
		List<DominioSituacaoVersaoDocumento> situacoes = new ArrayList<DominioSituacaoVersaoDocumento>();
		
		situacoes.add(DominioSituacaoVersaoDocumento.P);
		situacoes.add(DominioSituacaoVersaoDocumento.A);

		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(seqCirurgia);
		
		MbcProfCirurgias profCirurgia = getBlocoCirurgicoFacade().retornaResponsavelCirurgia(cirurgia);
		
		List<MbcAgendas> agenda = getBlocoCirurgicoFacade()
		.pesquisarCirurgiasAgendadasPorResponsavel(cirurgia, profCirurgia);

		List<AghVersaoDocumento> versaoDocumentos = getCertificacaoDigitalFacade().buscarVersaoSeqDoc(agenda.get(0).getSeq(), tipo, situacoes);
		if(versaoDocumentos != null &&versaoDocumentos.size() > 0){
			return versaoDocumentos.get(0).getSeq();
		}else{
			return null;
		}
	}
	
	public boolean validarGeracaoPendenciaAssinaturaPlanejamentoPaciente(Integer agdSeq) throws BaseException{
		desbloquearDocumentoCertificacaoPlanejamentoCirurgico(agdSeq);
		
		return verificarHabilitaPendenciaAssinaturaPlanejamentoCirurgico(false);
	}
	
	/**
	 * ORADB: P_DESBLOQ_DOC_CERTIF
	 * @throws AGHUNegocioExceptionSemRollback 
	 * @throws AGHUNegocioException 
	 */
	public void desbloquearDocumentoCertificacaoPlanejamentoCirurgico(Integer agdSeq) throws ApplicationBusinessException, ApplicationBusinessException {
		ICertificacaoDigitalFacade certificacaoDigitalFacade = getCertificacaoDigitalFacade();
		IPacienteFacade pacienteFacade = getPacienteFacade();
		
		//Verifica se está habilitada para uso de certificação digital
		boolean habilitaCertif = certificacaoDigitalFacade.verificaAssituraDigitalHabilitada();
		
		//Verifica se o usuário tem permissão para assinatura digital de documentos AGH RN002
		if(habilitaCertif){ 
			Boolean acaoQualifMatricula = pacienteFacade.verificarAcaoQualificacaoMatricula("ASSINATURA DIGITAL");
			if(acaoQualifMatricula){
				//Verificar se existem documentos de assinatura digital do paciente o planejamento cirúrgico
				List<AghVersaoDocumento> versoesDocumentos = certificacaoDigitalFacade.obterAghVersaoDocumentoPorAgenda(agdSeq, DominioTipoDocumento.PC);
				
				for(AghVersaoDocumento aghVersaoDocumento : versoesDocumentos) {
					aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
					certificacaoDigitalFacade.atualizarAghVersaoDocumento(aghVersaoDocumento);
				}
			}
		}
	}	
	
	
	/**
	 * @ORADB P_CERTIF_DIGITAL
	 * @param servidorLogado
	 * @param assinar
	 * @return
	 * @throws MECBaseException
	 */
	public boolean verificarHabilitaPendenciaAssinaturaPlanejamentoCirurgico(Boolean assinar) throws BaseException {
		
		ICertificacaoDigitalFacade certificacaoDigitalFacade = getCertificacaoDigitalFacade();

		if(Boolean.TRUE.equals(assinar)) {
			return false;
		}
		else {
			// Verifica se está habilitada para uso de certificação digital
			boolean habilitaCertif = certificacaoDigitalFacade.verificaAssituraDigitalHabilitada();
			return habilitaCertif;
		}
	}
	
	public List<LinhaReportVO> pesquisarComponentesSanguineosAgendas(Integer agdSeq) {
		return getBancoDeSangueFacade().pesquisarComponentesSanguineosAgendas(agdSeq);
	}
		
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}
	

	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}	
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
}
