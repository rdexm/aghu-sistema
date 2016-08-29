package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAtivoCancelado;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContasHospPacientesDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Triggers de <code>FAT_DADOS_CONTA_SEM_INT</code>
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatDadosContaSemIntRN extends AbstractAGHUCrudRn<FatDadosContaSemInt> {


	@EJB
	private ContaHospitalarON contaHospitalarON;
	
	
	
	private static final Log LOG = LogFactory.getLog(FatDadosContaSemIntRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private VFatContasHospPacientesDAO vFatContasHospPacientesDAO;
	
	@Inject
	private FatConvenioSaudeDAO fatConvenioSaudeDAO;
	
	@Inject
	private FatTipoAihDAO fatTipoAihDAO;
	
	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;
	
	@Inject
	private FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private FatContaInternacaoPersist fatContaInternacaoPersist;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
		
	/**
	 * ORADB: Trigger FATT_DCS_BRI
	 */
	@Override
	public boolean briPreInsercaoRow(FatDadosContaSemInt entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		if(entidade != null && entidade.getDataInicial() != null && entidade.getDataFinal() != null && CoreUtil.isMaiorDatas(entidade.getDataInicial(), entidade.getDataFinal())){
			FaturamentoExceptionCode.ERRO_DATA_FINAL_ANTERIOR_A_INICIAL_FAT_DADOS_CONTA_SEM_INTERNACAO.throwException();
		}
		
		/* Sequence setada na entidade */
		return true;
	}
	
	/**
	 * ORADB Trigger FATT_DCS_ASI
	 * @param dataFimVinculoServidor 
	 */
	@Override
	public boolean asiPosInsercaoStatement(final FatDadosContaSemInt entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return fatPEnforceDCSRules(DominioOperacaoBanco.INS, entidade, nomeMicrocomputador, dataFimVinculoServidor, Boolean.FALSE, servidorLogado);
	}

	/**
	 * Realiza insert <code>fatkDCSRnRnDcspAtuIns</code>.
	 * 
	 * ORADB Trigger fatk_dcs_rn.rn_dcsp_atu_ins
	 * @param servidorLogado 
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 * @throws IllegalStateException 
	 * 
	 */
	private void fatkDCSRnRnDcspAtuIns(final FatDadosContaSemInt fatDadosContaSemInt, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {

		final FatContasHospitalares conta = new FatContasHospitalares();
		final RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(fatDadosContaSemInt.getSerMatricula(), fatDadosContaSemInt.getSerVinCodigo()));
		conta.setServidorTemProfResponsavel(servidor);
		conta.setDataInternacaoAdministrativa(fatDadosContaSemInt.getDataInicial());
		conta.setContaManuseada(Boolean.FALSE);
		conta.setProcedimentoHospitalarInterno(null);
		conta.setDtAltaAdministrativa(fatDadosContaSemInt.getDataFinal());
		
		if(fatDadosContaSemInt.getEspSeq() != null){
			conta.setEspecialidade(this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(fatDadosContaSemInt.getEspSeq()));
		}
		
		final Short codigoConvenioSaude = this.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico().shortValue();
		final FatConvenioSaude fcs = getFatConvenioSaudeDAO().obterPorChavePrimaria(codigoConvenioSaude);
		conta.setConvenioSaude(fcs); //1  
		
		final byte seq = this.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO).getVlrNumerico().byteValue();
		final FatConvenioSaudePlano fcsp = getFatConvenioSaudePlanoDAO().obterPorChavePrimaria(new FatConvenioSaudePlanoId(fcs.getCodigo(), seq));
		conta.setConvenioSaudePlano(fcsp); //1

		final byte seqAIH = this.buscarAghParametro(AghuParametrosEnum.P_TIPO_AIH_NORMAL).getVlrNumerico().byteValue();
		final FatTipoAih tipoAIH = getFatTipoAihDAO().obterPorChavePrimaria(seqAIH);
		conta.setTipoAih(tipoAIH); // 1

		getContaHospitalarON().inserirContaHospitalar(conta, true, Boolean.FALSE, dataFimVinculoServidor);
		

		final FatContasInternacao contaInternacao = new FatContasInternacao();
		contaInternacao.setContaHospitalar(conta);
		contaInternacao.setDadosContaSemInt(fatDadosContaSemInt);
		final FatContaInternacaoPersist fatContaInternacaoPersist = getFatContaInternacaoPersist();
		fatContaInternacaoPersist.setComFlush(true);
		fatContaInternacaoPersist.inserir(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);
		
		fatDadosContaSemInt.setCthSeq(conta.getSeq());
	}
	
	
	
	
	
	// atualizacao

	@SuppressWarnings("ucd")
	@Override
	public boolean bruPreAtualizacaoRow(FatDadosContaSemInt original, FatDadosContaSemInt modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		if(modificada != null && modificada.getDataInicial() != null && modificada.getDataFinal() != null && CoreUtil.isMaiorDatas(modificada.getDataInicial(), modificada.getDataFinal())){
			FaturamentoExceptionCode.ERRO_DATA_FINAL_ANTERIOR_A_INICIAL_FAT_DADOS_CONTA_SEM_INTERNACAO.throwException();
		}
		
		return super.bruPreAtualizacaoRow(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);
	}
	/**
	 * ORADB Trigger FATT_DCS_ARU
	 */
	@Override
	public boolean aruPosAtualizacaoRow(final FatDadosContaSemInt original,
			final FatDadosContaSemInt modificada, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		/*
		if( CoreUtil.modificados(original.getSeq(), modificada.getSeq()) ||
			CoreUtil.modificados(original.getDataInicial(), modificada.getDataInicial()) ||
			CoreUtil.modificados(original.getDataFinal(), modificada.getDataFinal()) ||
			(original.getDataFinal() == null && modificada.getDataFinal() != null) ||
			(original.getDataFinal() != null && modificada.getDataFinal() == null) ||
			CoreUtil.modificados(original.getTipoCaraterInternacao(), modificada.getTipoCaraterInternacao()) ||
			CoreUtil.modificados(original.getSerMatricula(), modificada.getSerMatricula()) ||
			CoreUtil.modificados(original.getSerVinCodigo(), modificada.getSerVinCodigo()) ||
			CoreUtil.modificados(original.getPacCodigo(), modificada.getPacCodigo()) ||
			CoreUtil.modificados(original.getIndSituacao(), modificada.getIndSituacao())  ) {
		
		 * fatK_dcs.push_dcs_row(:new.rowid      ,
			                    :old.seq      ,
			                    :old.data_inicial      ,
			                    :old.data_final      ,
			                    :old.tci_seq      ,
			                    :old.ser_matricula      ,
			                    :old.ser_vin_codigo      ,
			                    :old.pac_codigo      ,
			                    :old.ind_situacao      );
		
			
		}
		
		 */
		
		return true;
	}
	
	/**
	 * ORADB Trigger FATT_DCS_ASU
	 * @param dataFimVinculoServidor 
	 */
	@Override
	public boolean asuPosAtualizacaoStatement(final FatDadosContaSemInt original, final FatDadosContaSemInt modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return fatPEnforceDCSRules(DominioOperacaoBanco.UPD, modificada, nomeMicrocomputador, dataFimVinculoServidor, Boolean.FALSE, servidorLogado);
	}

	public boolean asuPosAtualizacaoStatement(final FatDadosContaSemInt original, final FatDadosContaSemInt modificada, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores servidorLogado) throws BaseException {
		return fatPEnforceDCSRules(DominioOperacaoBanco.UPD, modificada, nomeMicrocomputador, dataFimVinculoServidor, 
				substituirProntuario, servidorLogado);
	}
	
	/**
	 * Realiza insert <code>fatkDCSRnRnDcspAtuUpd</code>.
	 * 
	 * ORADB Trigger fatk_dcs_rn.rn_dcsp_atu_upd
	 * @param servidorLogado 
	 * @throws BaseException 
	 * 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void fatkDCSRnRnDcspAtuUpd(final FatDadosContaSemInt fatDadosContaSemInt, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		
		final FatContasInternacao conta = getFatContasInternacaoDAO().obterFatContasInternacaoPorContasHospitalaresPorDCSSeq(fatDadosContaSemInt.getSeq(), fatDadosContaSemInt.getCthSeq());
		
		final Object []dominios = {DominioSituacaoConta.A,DominioSituacaoConta.F}; 
		if(CoreUtil.notIn(conta.getContaHospitalar().getIndSituacao(), dominios)){
			FaturamentoExceptionCode.MENSAGEM_ERRO_UPDATE_FAT_DADOS_CONTA_SEM_INTERNACAO.throwException();
			
		} else {
			final FatContasHospitalares oldCtaHosp;
			final FatContasHospitalares newCtaHosp = conta.getContaHospitalar();		
			
			try{
				oldCtaHosp = getFaturamentoFacade().clonarContaHospitalar(newCtaHosp);
			}catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}
			
			if(DominioSituacaoConta.C.toString().equals(fatDadosContaSemInt.getIndSituacao().toString())){
				newCtaHosp.setIndSituacao(DominioSituacaoConta.C);
			}
			
			boolean cthAlterada = false;
			
			// campo obrigatório
			if(!fatDadosContaSemInt.getDataInicial().equals(oldCtaHosp.getDataInternacaoAdministrativa())){
				cthAlterada = true;
			}
			
			if(fatDadosContaSemInt.getDataFinal() != null){
				// data de alta alterada
				if(oldCtaHosp.getDtAltaAdministrativa() != null && !oldCtaHosp.getDtAltaAdministrativa().equals(fatDadosContaSemInt.getDataFinal())){
					cthAlterada = true;
					
				// data de alta criada
				} else if(oldCtaHosp.getDtAltaAdministrativa() == null){
					cthAlterada = true;
				}
				
			// data de alta removida
			} else if(oldCtaHosp.getDtAltaAdministrativa() != null){
				cthAlterada = true;
			}
			
			if(fatDadosContaSemInt.getEspSeq() != null){
				// especialidade alterada
				if(oldCtaHosp.getEspecialidade() != null && !(oldCtaHosp.getEspecialidade().getSeq() == fatDadosContaSemInt.getEspSeq())){
					cthAlterada = true;
					
				// especialidade criada
				} else if(oldCtaHosp.getEspecialidade() == null){
					cthAlterada = true;
				}
				
			// especialidade removida
			} else if(oldCtaHosp.getEspecialidade() != null){
				cthAlterada = true;
			}
			
			final RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(fatDadosContaSemInt.getSerMatricula(), fatDadosContaSemInt.getSerVinCodigo()));
			
			if(CoreUtil.modificados(servidor, oldCtaHosp.getServidorTemProfResponsavel())){
				newCtaHosp.setServidorTemProfResponsavel(servidor);
				cthAlterada = true;
			}
			
			if(cthAlterada){
				newCtaHosp.setEspecialidade((fatDadosContaSemInt.getEspSeq() != null) ? this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(fatDadosContaSemInt.getEspSeq()) : null);
				newCtaHosp.setDtAltaAdministrativa(fatDadosContaSemInt.getDataFinal());
				newCtaHosp.setDataInternacaoAdministrativa(fatDadosContaSemInt.getDataInicial());
				getFaturamentoFacade().persistirContaHospitalar(newCtaHosp, oldCtaHosp, Boolean.TRUE, Boolean.FALSE, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor);
				
				fatDadosContaSemInt.setEspSeq((newCtaHosp.getEspecialidade() == null) ? null : newCtaHosp.getEspecialidade().getSeq());
			}
		}
		 
		
		/*  PROCEDURE RN_DCSP_ATU_UPD
			 (P_SEQ IN NUMBER
			 ,P_IND_SITUACAO IN VARCHAR2
			 )
			 IS
			cursor c_conta is
			  select cth.seq ,cth.ind_situacao
			  from fat_contas_hospitalares cth ,
			       fat_contas_internacao   coi
			  where coi.dcs_seq = p_seq
			  and   cth.seq     = coi.cth_seq;
			
			reg_conta       c_conta%rowtype;
			
			BEGIN
			  open c_conta;
			  fetch c_conta into reg_conta;
			  close c_conta;
			
			  if reg_conta.ind_situacao not in ('A','F') then
			     raise_application_error(-20000,'Alteração não permitida. Conta faturada !');
			  end if;
			
			  if p_ind_situacao = 'C' then
			     begin
			     update fat_contas_hospitalares
			        set ind_situacao = 'C'
			      where seq = reg_conta.seq;
			     exception when others then
			       raise_application_error(-20000,'Erro no cancelamento da conta.'||sqlerrm);
			     end;
			  end if;
			
			END;
		 */
	}

	
	
	// deleção
	/**
	 * ORADB Trigger FATT_DCS_ASD
	 */
	@Override
	public boolean asdPosRemocaoStatement(final FatDadosContaSemInt entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		/*
		 TRIGGER "AGH".FATT_DCS_ASD
		 AFTER DELETE
		 ON FAT_DADOS_CONTA_SEM_INT
		// QMS$CIRCUMVENT_MUTATING_TABLE_RESTRICTION /
		
		begin
		   fatK_dcs.process_dcs_rows('DELETE');
		end;
		 */
		return true;
	}
	
	
	
	/**
	 * Código da Enforce
	 * 
	 * ORADB Procedure fatP_enforce_dcs_rules
	 * @param servidorLogado 
	 * @throws BaseException 
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private boolean fatPEnforceDCSRules(final DominioOperacaoBanco event, final FatDadosContaSemInt entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores servidorLogado) throws BaseException{
		if(DominioOperacaoBanco.UPD.equals(event)){
			if(!substituirProntuario){
				fatkDCSRnRNDcspVerRegras(entidade);
				fatkDCSRnRnDcspAtuUpd(entidade, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor);
				return true;
			}
			
		} else if(DominioOperacaoBanco.INS.equals(event)){
			fatkDCSRnRNDcspVerRegras(entidade);
			fatkDCSRnRnDcspAtuIns(entidade, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor);
			return true;
		}
		
		return false;
	}

	/**
	 * Verifica regras para inclusão ,alteração e deleção na tabela <code>fatkDCSRnRNDcspVerRegras</code>.
	 * 
	 * ORADB Trigger fatk_dcs_rn.rn_dcsp_ver_regras
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private void fatkDCSRnRNDcspVerRegras(final FatDadosContaSemInt fatDadosContaSemInt) throws ApplicationBusinessException {

		final List<Integer> list = getVFatContasHospPacientesDAO().pesquisarContasParaValidarRegrasFatDadosContaSemInt( fatDadosContaSemInt.getPacCodigo(), 
																														fatDadosContaSemInt.getSeq(), 
																														fatDadosContaSemInt.getDataInicial(),
																														fatDadosContaSemInt.getDataFinal());
		
		if(list != null && !list.isEmpty()){
			FaturamentoExceptionCode.FAT_00810.throwException();
		}
	}
	
	/**
	 * ORADB FATC_GERA_CONTA_SEM_INT
	 * @throws BaseException 
	 */
	@SuppressWarnings("ucd")
	public FatDadosContaSemInt fatcGeraContaSemInt(final Short seqEspecialidade, final Date dtRealizado, final AipPacientes paciente, AghUnidadesFuncionais un, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		AghEspecialidades especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);
		return fatcGeraContaSemInt(especialidade, dtRealizado, paciente, un, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * ORADB FATC_GERA_CONTA_SEM_INT
	 * @throws BaseException 
	 */
	public FatDadosContaSemInt fatcGeraContaSemInt(final AghEspecialidades especialidade, final Date dtRealizado, final AipPacientes paciente, AghUnidadesFuncionais un, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		FatDadosContaSemInt contaSemInt = new FatDadosContaSemInt();
		contaSemInt.setDataInicial(dtRealizado);
		contaSemInt.setDataFinal(dtRealizado);
		
		/* #45758 - FUNCTION FATC_GERA_CONTA_SEM_INT passa 2 como parametro */
		contaSemInt.setTipoCaraterInternacao(getInternacaoFacade().obterAinTiposCaraterInternacao(2));
		contaSemInt.setSerMatricula(especialidade.getServidorChefe().getId().getMatricula());
		contaSemInt.setSerVinCodigo(especialidade.getServidorChefe().getId().getVinCodigo());
		contaSemInt.setPacCodigo(paciente.getCodigo());
		contaSemInt.setPaciente(paciente);
		contaSemInt.setIndSituacao(DominioAtivoCancelado.A);
		contaSemInt.setUnfSeq(un.getSeq());
		
		getFaturamentoFacade().inserirFatDadosContaSemInt(contaSemInt, nomeMicrocomputador, dataFimVinculoServidor);
		
		return contaSemInt;
	}

	private VFatContasHospPacientesDAO getVFatContasHospPacientesDAO(){
		return vFatContasHospPacientesDAO;
	}
	
	private FatConvenioSaudeDAO getFatConvenioSaudeDAO(){
		return fatConvenioSaudeDAO;
	}
	
	private FatTipoAihDAO getFatTipoAihDAO(){
		return fatTipoAihDAO;
	}
	
	private FatConvenioSaudePlanoDAO getFatConvenioSaudePlanoDAO(){
		return fatConvenioSaudePlanoDAO;
	}
	
	private FatContaInternacaoPersist getFatContaInternacaoPersist() {
		return fatContaInternacaoPersist;
	}
	
	private ContaHospitalarON getContaHospitalarON(){
		return contaHospitalarON;
	}
	
	private FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	public AghParametros buscarAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.parametroFacade.buscarAghParametro(parametrosEnum);
	}
	
}
