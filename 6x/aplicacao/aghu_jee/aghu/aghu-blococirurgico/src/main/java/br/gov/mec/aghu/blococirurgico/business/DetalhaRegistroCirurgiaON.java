package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class DetalhaRegistroCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DetalhaRegistroCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;
	
	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;
	
	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -2357776561023714711L;
	
	protected enum DetalhaRegistroCirurgiaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_MAIOR_INICIO,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_INICIO_MAIOR_FIM,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_FIM_MAIOR_SAIDA_SALA,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_MAIOR_IGUAL_SAIDA_SALA,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_MAIOR_IGUAL_SAIDA_SALA_RECUPERACAO,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_SAIDA_SALA_PREENCHIDA_SEM_ENTRADA_SALA,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_SAIDA_SALA_MAIOR_ENTRADA_SALA_RECUPERACAO,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_MAIOR_SAIDA_SALA_RECUPERACAO,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_FIM_CIRURGIA_PREENCHIDA_SEM_INICIO_CIRURGIA,
		MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_PREENCHIDA_SEM_SAIDA_SALA_RECUPERACAO
		;
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarProcedimentoCirurgicoEscalaCirurgica(MbcCirurgias cirurgia){
		List<MbcProcEspPorCirurgias> mbcProcEspPorCirurgiasList;
		if(cirurgia.getDigitaNotaSala()){
			mbcProcEspPorCirurgiasList = getMbcProcEspPorCirurgiasDAO().getPesquisarProcedimentoCirurgicoEscalaCirurgica(cirurgia.getSeq(), DominioIndRespProc.NOTA);
		}else{
			mbcProcEspPorCirurgiasList = getMbcProcEspPorCirurgiasDAO().getPesquisarProcedimentoCirurgicoEscalaCirurgica(cirurgia.getSeq(), DominioIndRespProc.AGND); 
		}
		return mbcProcEspPorCirurgiasList;
	}
		
	public void atualizarUnidadeFuncionalProfCirurgias(MbcCirurgias cirurgia){
		List<MbcProfCirurgias> listMbcProfCirurgias = mbcProfCirurgiasDAO.pesquisarMbcProfCirurgiasByCirurgia(cirurgia.getSeq());
		AghUnidadesFuncionais aghUnidadesFuncionais = iAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(cirurgia.getUnidadeFuncional().getSeq());
		
		for(MbcProfCirurgias mbcProfCirugia : listMbcProfCirurgias){
			
			MbcProfCirurgiasId profCirurgiaId = new MbcProfCirurgiasId();
			profCirurgiaId.setCrgSeq(cirurgia.getSeq());		
			
			profCirurgiaId.setPucSerVinCodigo(mbcProfCirugia.getId().getPucSerVinCodigo());
			profCirurgiaId.setPucSerMatricula(mbcProfCirugia.getId().getPucSerMatricula());
			profCirurgiaId.setPucIndFuncaoProf(mbcProfCirugia.getId().getPucIndFuncaoProf());
			profCirurgiaId.setPucUnfSeq(aghUnidadesFuncionais.getSeq());
			
			MbcProfCirurgias profCirurgia = new MbcProfCirurgias();
			profCirurgia.setId(profCirurgiaId);
			profCirurgia.setCirurgia(cirurgia);
			profCirurgia.setServidorPuc(mbcProfCirugia.getServidorPuc());
			profCirurgia.setUnidadeFuncional(aghUnidadesFuncionais);
			profCirurgia.setFuncaoProfissional(mbcProfCirugia.getFuncaoProfissional());
			profCirurgia.setIndRealizou(mbcProfCirugia.getIndRealizou());
			profCirurgia.setIndResponsavel(mbcProfCirugia.getIndResponsavel());
			profCirurgia.setIndInclEscala(mbcProfCirugia.getIndInclEscala());
			profCirurgia.setCriadoEm(mbcProfCirugia.getCriadoEm());			
			profCirurgia.setServidor(mbcProfCirugia.getServidor());
			
			
			
			MbcProfAtuaUnidCirgsId mbcProfAtuaUnidCirgsId = new MbcProfAtuaUnidCirgsId();
			mbcProfAtuaUnidCirgsId.setSerVinCodigo(mbcProfCirugia.getId().getPucSerVinCodigo());
			mbcProfAtuaUnidCirgsId.setSerMatricula(mbcProfCirugia.getId().getPucSerMatricula());
			mbcProfAtuaUnidCirgsId.setIndFuncaoProf(mbcProfCirugia.getId().getPucIndFuncaoProf());
			mbcProfAtuaUnidCirgsId.setUnfSeq(aghUnidadesFuncionais.getSeq());
			
			MbcProfAtuaUnidCirgs profAtuaUniCirID = mbcProfAtuaUnidCirgsDAO.obterMbcProfAtuaUnidCirgs(mbcProfAtuaUnidCirgsId);
			
			
			if(profAtuaUniCirID == null) {
			
				MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgsOld = mbcProfAtuaUnidCirgsDAO.obterMbcProfAtuaUnidCirgs(mbcProfCirugia.getMbcProfAtuaUnidCirgs().getId());
				
				MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
				
				mbcProfAtuaUnidCirgs.setId(mbcProfAtuaUnidCirgsId);
				mbcProfAtuaUnidCirgs.setUnidadeFuncional(mbcProfAtuaUnidCirgsOld.getUnidadeFuncional());
				mbcProfAtuaUnidCirgs.setServidorCadastrado(mbcProfAtuaUnidCirgsOld.getServidorCadastrado());
				mbcProfAtuaUnidCirgs.setServidorAlteradoPor(mbcProfAtuaUnidCirgsOld.getServidorAlteradoPor());
				mbcProfAtuaUnidCirgs.setSituacao(mbcProfAtuaUnidCirgsOld.getSituacao());
				mbcProfAtuaUnidCirgs.setCriadoEm(mbcProfAtuaUnidCirgsOld.getCriadoEm());
				mbcProfAtuaUnidCirgs.setRapServidores(mbcProfAtuaUnidCirgsOld.getRapServidores());
				mbcProfAtuaUnidCirgs.setIndFuncaoProf(mbcProfAtuaUnidCirgsOld.getIndFuncaoProf());
				mbcProfAtuaUnidCirgs.setMbcProfAtuaUnidCirgs(null);
				
				mbcProfAtuaUnidCirgsDAO.persistir(mbcProfAtuaUnidCirgs);
			
			}	
			
			
			
			mbcProfCirurgiasDAO.remover(mbcProfCirugia);
			mbcProfCirurgiasDAO.flush();
			mbcProfCirurgiasDAO.persistir(profCirurgia);
		}
		
		
	}
	
	
	public String persistirAcompanhamentoMbcCirurgias(MbcCirurgias cirurgia, Boolean inserirAtendimento) throws ApplicationBusinessException, BaseException {
		//Movida a lógica para a ON, pois se fizermos via controller, o commit de persistencia em MbcCirurgias será executado e caso ocorra erro ao inserir atendimento não será feito o rollback
		if (inserirAtendimento && cirurgia.getAtendimento() == null) {
			validarMbcCirurgias(cirurgia);
			getBlocoCirurgicoFacade().inserirAtendimentoPreparoCirurgia(cirurgia.getPaciente(), cirurgia, null);
		}else {
			persistirMbcCirurgias(cirurgia);
		}
		
		return "LABEL_DETALHAR_REGISTRO_CIRURGIA_GRAVADO_SUCESSO";
	}
	
	
	
	public String persistirMbcCirurgias(MbcCirurgias cirurgia) throws ApplicationBusinessException, BaseException {
		validarMbcCirurgias(cirurgia);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		getBlocoCirurgicoFacade().persistirCirurgia(cirurgia, servidorLogado);
		atualizarUnidadeFuncionalProfCirurgias(cirurgia);
		return "LABEL_DETALHAR_REGISTRO_CIRURGIA_GRAVADO_SUCESSO";
	}

	public void validarMbcCirurgias(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if(DateUtil.validaDataMaior(cirurgia.getDataEntradaSala(), cirurgia.getDataInicioCirurgia())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_MAIOR_INICIO);
		}
		if(DateUtil.validaDataMaiorIgual(cirurgia.getDataInicioCirurgia(), cirurgia.getDataFimCirurgia())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_INICIO_MAIOR_FIM);
		}
		if(DateUtil.validaDataMaior(cirurgia.getDataFimCirurgia(), cirurgia.getDataSaidaSala())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_FIM_MAIOR_SAIDA_SALA);
		}
		if(DateUtil.validaDataMaiorIgual(cirurgia.getDataEntradaSala(), cirurgia.getDataSaidaSala())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_MAIOR_IGUAL_SAIDA_SALA);
		}
		if(DateUtil.validaDataMaiorIgual(cirurgia.getDataEntradaSr(), cirurgia.getDataSaidaSr())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_MAIOR_IGUAL_SAIDA_SALA_RECUPERACAO);
		}
		validarMbcCirurgiasEntradaSaida(cirurgia);
	}

	private void validarMbcCirurgiasEntradaSaida(MbcCirurgias cirurgia)
			throws ApplicationBusinessException {
		if(DateUtil.validaDataMaior(cirurgia.getDataSaidaSala(), cirurgia.getDataEntradaSr())){
			String dtSaidaSala = DateUtil.obterDataFormatadaHoraMinutoSegundo(cirurgia.getDataSaidaSala());
			String dtSaidaSr = DateUtil.obterDataFormatadaHoraMinutoSegundo(cirurgia.getDataEntradaSr());
			if (!dtSaidaSala.equalsIgnoreCase(dtSaidaSr)){
				throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_SAIDA_SALA_MAIOR_ENTRADA_SALA_RECUPERACAO);
			}	
		}
		if(DateUtil.validaDataMaior(cirurgia.getDataEntradaSr(), cirurgia.getDataSaidaSr())){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_MAIOR_SAIDA_SALA_RECUPERACAO);
		}
		if(cirurgia.getDataSaidaSala() !=null && cirurgia.getDataEntradaSala() == null){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_SAIDA_SALA_PREENCHIDA_SEM_ENTRADA_SALA);
		}
		if(cirurgia.getDataFimCirurgia() !=null && cirurgia.getDataInicioCirurgia() == null){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_FIM_CIRURGIA_PREENCHIDA_SEM_INICIO_CIRURGIA);
		}
		if(cirurgia.getDataSaidaSr() !=null && cirurgia.getDataEntradaSr() == null){
			throw new ApplicationBusinessException(DetalhaRegistroCirurgiaONExceptionCode.MENSAGEM_DETALHAR_REGISTRO_CIRURGIA_ENTRADA_SALA_RECUPERACAO_PREENCHIDA_SEM_SAIDA_SALA_RECUPERACAO);
		}
	}
	
	public String mbccIdadeExtFormat(Date dtNascimento, Date data){		
		String tempo = "anos";
		String tempoMes = "meses";
		Integer idadeMes = null;
		String idadeFormat = null;
		if (dtNascimento != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dtNascimento);
			Calendar dataReferencia = new GregorianCalendar();
			dataReferencia.setTime(data);
			
			Integer idadeNum = dataReferencia.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);			

			if (dataReferencia.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataReferencia.get(Calendar.MONTH) == dataNascimento
					.get(Calendar.MONTH)
					&& dataReferencia.get(Calendar.DAY_OF_MONTH) < dataNascimento
							.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}
			
			if (dataReferencia.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeMes = dataReferencia.get(Calendar.MONTH)
						+ (12 - dataNascimento.get(Calendar.MONTH));
			} else {
				idadeMes = dataReferencia.get(Calendar.MONTH)
						- dataNascimento.get(Calendar.MONTH);
				if (dataReferencia.get(Calendar.DAY_OF_MONTH) < dataNascimento
						.get(Calendar.DAY_OF_MONTH)) {
					idadeMes--;
				}
			}
			
			if(idadeMes<0){
				if (12+(idadeMes) == 1) {
					tempoMes = 12+(idadeMes) + " mês";
				} else {
					tempoMes = 12+(idadeMes) + " meses";
				}	
			}else{
				if (idadeMes == 1) {
					tempoMes = idadeMes + " mês";
				} else {
					tempoMes = idadeMes + " meses";
				}
			}
			
			idadeFormat = idadeNum + " " + tempo + " " + tempoMes;
		}
		return idadeFormat;	
	}

	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	
}
