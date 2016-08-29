package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoJnDAO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedAmbRealizadoJn;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Triggers de:<br/>
 * ORADB: <code>FAT_PROCED_AMB_REALIZADOS</code>
 * 
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta","PMD.CyclomaticComplexity"})
@Stateless
public class ProcedimentosAmbRealizadoRN extends AbstractAGHUCrudRn<FatProcedAmbRealizado> {

	private static final Log LOG = LogFactory.getLog(ProcedimentosAmbRealizadoRN.class);
	
	@EJB
	private FaturamentoFatkInterfaceAacRN faturamentoFatkInterfaceAacRN;
	
	@EJB
	private ItemContaHospitalarRN itemContaHospitalarRN;
	
	@EJB
	private FaturamentoFatkPmrRN faturamentoFatkPmrRN;
	
	@EJB
	private CidContaHospitalarON cidContaHospitalarON;
	
	@EJB
	private FaturamentoFatkEpsRN faturamentoFatkEpsRN;
	
	@EJB
	private FaturamentoFatkCpeRN faturamentoFatkCpeRN;
	
	@EJB
	private ContaHospitalarRN contaHospitalarRN;
	
	@EJB
	private FaturamentoFatkCapRN faturamentoFatkCapRN;
	
	@EJB
	private FaturamentoFatkPhiRN faturamentoFatkPhiRN;

	public enum ProcedimentosAmbRealizadoRNExceptionCode implements BusinessExceptionCode {
		FAT_00453, FATT_PMR_BRD_JA_PROCESSADO;
	}
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatProcedAmbRealizadoJnDAO fatProcedAmbRealizadoJnDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private FatContasHospitalaresDAO  fatContasHospitalaresDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final long serialVersionUID = -8938099939930799977L;

	@SuppressWarnings("PMD.NPathComplexity")
	private void inserirFatProcedAmbRealizadoJn(final FatProcedAmbRealizado proc, final DominioOperacoesJournal operacao) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		 FatProcedAmbRealizadoJn jn = new FatProcedAmbRealizadoJn();
		 
		 jn.setNomeUsuario(servidorLogado.getUsuario());
		 jn.setOperacao(operacao);
		 
		 jn.setSeq(proc.getSeq());
		 jn.setAlteradoPor(proc.getAlteradoPor());
		 jn.setAlteradoEm(proc.getAlteradoEm());
		 jn.setCriadoPor(proc.getCriadoPor());
		 jn.setCriadoEm(proc.getCriadoEm());
		 jn.setDthrRealizado(proc.getDthrRealizado());
		 jn.setIndSituacao(proc.getSituacao().getCodigo());
		 jn.setLocalCobranca(proc.getLocalCobranca().toString());
		 jn.setQuantidade(proc.getQuantidade()); 
		 jn.setValor(proc.getValor());
		 
		 if (proc.getFatCompetencia() != null) {
			 jn.setCpeDtHrInicio(proc.getFatCompetencia().getId().getDtHrInicio());
			 jn.setCpeModulo(proc.getFatCompetencia().getId().getModulo().toString());
			 jn.setCpeMes(proc.getFatCompetencia().getId().getMes().byteValue()); 
			 jn.setCpeAno(proc.getFatCompetencia().getId().getAno().shortValue());
		 }
		  
		 if(proc.getItemSolicitacaoExame() != null){
			 jn.setIseSeqp(proc.getItemSolicitacaoExame().getId().getSeqp());
			 jn.setIseSoeSeq(proc.getItemSolicitacaoExame().getId().getSoeSeq());
		 }
		  
		 if(proc.getConsultaProcedHospitalar() != null){
			 jn.setPrhPhiSeq(proc.getConsultaProcedHospitalar().getId().getPhiSeq()); // CONFIRMAR ISSO
			 jn.setPrhConNumero(proc.getConsultaProcedHospitalar().getId().getConNumero()); 
		 }
		  
		if (proc.getProcedimentoHospitalarInterno() != null) {
			jn.setPhiSeq(proc.getProcedimentoHospitalarInterno().getSeq());
		}

		if (proc.getServidor() != null) {
			jn.setSerMatricula(proc.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(proc.getServidor().getId().getVinCodigo());
		}

		if (proc.getUnidadeFuncional() != null) {
			jn.setUnfSeq(proc.getUnidadeFuncional().getSeq());
		}

		if (proc.getEspecialidade() != null) {
			jn.setEspSeq(proc.getEspecialidade().getSeq());
		}

		if (proc.getPaciente() != null) {
			jn.setPacCodigo(proc.getPaciente().getCodigo());
		}
		 
		 if(proc.getConvenioSaudePlano() != null){
			 jn.setCspSeq(proc.getConvenioSaudePlano().getId().getSeq());
			 jn.setCspCnvCodigo (proc.getConvenioSaudePlano().getId().getCnvCodigo()); 
		 }
		 
		 if (proc.getIndOrigem() != null) {
			 jn.setIndOrigem(proc.getIndOrigem().toString());
		 }
		 jn.setPpcCrgSeq(proc.getPpcCrgSeq());
		 jn.setPpcEprPciSeq(proc.getPpcEprPciSeq());
		 jn.setPpcEprEspSeq(proc.getPpcEprEspSeq());
		 
		 if (proc.getPpcIndRespProc() != null) {
			 jn.setPpcIndRespProc(proc.getPpcIndRespProc().toString());
		 }
		 if (proc.getIndPendente() != null) {
			 jn.setIndPendente(proc.getIndPendente() ? "S" : "N");
		 }
		 if(proc.getAtendimento() != null) {
			 jn.setAtdSeq(proc.getAtendimento().getSeq());
		 }
		 jn.setQtdeFaturada(proc.getQtdeFaturada());
		 
		 getFatProcedAmbRealizadoJnDAO().persistir(jn);
		 getFatProcedAmbRealizadoJnDAO().flush();
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_PMR_ARD</code>
	 * </p>
	 * Se usuário for = "BACKUP", que faz a limpeza, Não executar lógica de
	 * " DELETE"
	 */	
	public boolean ardPosRemocaoRow(FatProcedAmbRealizado entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		// IF aghc_get_user_banco = 'BACKUP' THEN RETURN; END IF;

		// if fatk_pmr_rn.v_pmr_journal then
		if(fatVariaveisVO != null && !Boolean.FALSE.equals(fatVariaveisVO.getvPmrJournal())){
			//final FatProcedAmbRealizado oldFatProcedAmbRealizado = getFatProcedAmbRealizadoDAO().obterOriginal(entidade.getSeq());
			inserirFatProcedAmbRealizadoJn(entidade, DominioOperacoesJournal.DEL);
		}
		
		return true;
	}
	
	/**
	 * TODO
	 * <p>
	 * ORADB: <code>FATT_PMR_ARU</code>
	 * </p>
	 */	
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public boolean aruPosAtualizacaoRow(final FatProcedAmbRealizado original, final FatProcedAmbRealizado modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		boolean result = false;
		
		if(CoreUtil.modificados(modificada.getSeq(), original.getSeq())){
			result = true;

		} else if(CoreUtil.modificados(modificada.getAlteradoPor(), original.getAlteradoPor())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getAlteradoEm(), original.getAlteradoEm())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getCriadoPor(), original.getCriadoPor())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getCriadoEm(), original.getCriadoEm())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getDthrRealizado(), original.getDthrRealizado())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getSituacao(), original.getSituacao())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getLocalCobranca(), original.getLocalCobranca())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getQuantidade(), original.getQuantidade())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getValor(), original.getValor())){
			result = true;
		
		} else if(CoreUtil.modificados(modificada.getFatCompetencia(), original.getFatCompetencia())){ 
			result = true;
		 
		} else if(CoreUtil.modificados(modificada.getItemSolicitacaoExame(), original.getItemSolicitacaoExame())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getConsultaProcedHospitalar(), original.getConsultaProcedHospitalar())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getProcedimentoHospitalarInterno(), original.getProcedimentoHospitalarInterno())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getServidor(), original.getServidor())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getUnidadeFuncional(), original.getUnidadeFuncional())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getEspecialidade().getSeq(), original.getEspecialidade().getSeq())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getPaciente(), original.getPaciente())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getConvenioSaudePlano(), original.getConvenioSaudePlano())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getIndOrigem(), original.getIndOrigem())){
			result = true;

		} else if(CoreUtil.modificados(modificada.getPpcCrgSeq(), original.getPpcCrgSeq())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getPpcEprPciSeq(), original.getPpcEprPciSeq())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getPpcEprEspSeq(), original.getPpcEprEspSeq())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getPpcIndRespProc(), original.getPpcIndRespProc())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getIndPendente(), original.getIndPendente())){
			result = true;
			
		} else if(CoreUtil.modificados(modificada.getAtendimento(), original.getAtendimento())){
			result = true;
		}
		
		if(result){
			if(fatVariaveisVO != null && !Boolean.FALSE.equals(fatVariaveisVO.getvPmrJournal())){
				inserirFatProcedAmbRealizadoJn(original, DominioOperacoesJournal.UPD);
			}
		}
		
		return result;
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_PMR_ASU</code>
	 * </p>
	 * @param dataFimVinculoServidor 
	 */
	@Override
	public boolean asuPosAtualizacaoStatement(final FatProcedAmbRealizado original, final FatProcedAmbRealizado modificada, String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws BaseException {

		getFaturamentoFatkPmrRN().processPmrUpdate(original, modificada);
		return super.asuPosAtualizacaoStatement(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_PMR_BASE_BRI</code> <br/>
	 * ORADB: <code>FATT_PMR_BRI</code> <br/>
	 * </p>
	 * @param dataFimVinculoServidor 
	 */
	@Override
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public boolean briPreInsercaoRow(FatProcedAmbRealizado entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		// Verifica se procedimento interno referenciada pelas duas FKs sao
		// iguais
		final Integer phi = entidade.getProcedimentoHospitalarInterno() != null ? entidade.getProcedimentoHospitalarInterno().getSeq() : null; 
		getFaturamentoFatkPmrRN().rnPmrpVerUniPint(phi, entidade.getConsultaProcedHospitalar() == null ? null : entidade.getConsultaProcedHospitalar().getId().getPhiSeq());
			
		// VERIFICA SE O PHI ESTÁ ATIVO
		// SO VERIFICA PHI ATIVO PARA ITENS NOVOS (COM CRIADO_EM NULL)
		if(entidade.getCriadoEm() == null && !getFaturamentoFatkPhiRN().rnPhicVerAtivo(entidade.getProcedimentoHospitalarInterno())){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00625); 
		}
		
		//Atualiza a Competência caso não preenchida
		atualizarCompetencia(entidade);
		
		// Atualiza o servidor que está inserindo o procedimento
		// ambulatorial
		atualizarServidor(entidade, dataFimVinculoServidor);
		
		// Atualiza os dados de criação da entidade (criador quando por
		// quem, etc).
		atualizarDadosCriacao(entidade);
		
		// Verifica se o convenio e plano sao validos para esse procedimento
		getContaHospitalarRN().verificarFiltroConvPlano(null, null, entidade.getSeq(),
													     entidade.getConvenioSaudePlano() != null ? entidade.getConvenioSaudePlano().getId().getCnvCodigo() : null, 
													     entidade.getConvenioSaudePlano() != null ? entidade.getConvenioSaudePlano().getId().getSeq() : null, 
				entidade.getProcedimentoHospitalarInterno() != null ? entidade.getProcedimentoHospitalarInterno().getSeq() : null, null, null,
				(entidade.getIndOrigem() != null) ? entidade.getIndOrigem().toString() : null);
		
		return Boolean.TRUE;
	}

	public void posInsercaoRow(FatProcedAmbRealizado entidade, String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException, BaseException {
		
		String tipoCirurgia = null; // v_tipo_cirurgia
		// mbc_cirurgias.natureza_agend%type :=
		// ' ';
		String ufPaciente = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		// Sendo cirurgia verifica se é urgente e origem do paciente
		if(DominioOrigemProcedimentoAmbulatorialRealizado.CIA.equals(entidade.getIndOrigem())){
		MbcCirurgias mbcCir = entidade.getPpcCrgSeq() != null ? getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(entidade.getPpcCrgSeq()) : null;
		tipoCirurgia = mbcCir != null ? mbcCir.getNaturezaAgenda().name() : null;
		
		VAipEnderecoPaciente vAipEnderecoPaciente = this.getCadastroPacienteFacade().obterEndecoPaciente(entidade.getPaciente().getCodigo());
		
			if(vAipEnderecoPaciente != null){
				ufPaciente = vAipEnderecoPaciente.getUf();
			} 
		}
		
		// Verifica se o procedimento deve ser cobrado de AIH -- ETB 210706
		// Verifica o relacionamento com o plano de internação
		
		String ufPadraoHU = buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU).getVlrTexto();
		
		if (getContaHospitalarRN().fatcVerCaractPhiQrInt(
						entidade.getConvenioSaudePlano() != null ? entidade.getConvenioSaudePlano().getId().getCnvCodigo() : null,
						Byte.valueOf("1"), 
						entidade.getProcedimentoHospitalarInterno() != null ? entidade.getProcedimentoHospitalarInterno().getSeq() : null, 
						DominioFatTipoCaractItem.COBRA_EM_AIH.getDescricao())
				&& DominioOrigemProcedimentoAmbulatorialRealizado.CIA.equals(entidade.getIndOrigem())
				&& !"URG".equalsIgnoreCase(tipoCirurgia)
				&& ufPadraoHU.equalsIgnoreCase(ufPaciente)) {
				/*
				 * IF FATC_VER_CARACT_PHI_QR_int(:new.CSP_CNV_CODIGO,1,
				 * :new.PHI_SEQ,'Cobra Em AIH') = 'S' AND :new.ind_origem =
				 * 'CIA' AND v_tipo_cirurgia <> 'URG' AND v_uf_paciente = 'RS'
				 * THEN
				 */
				entidade.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
				DominioSituacaoConta dominiosSitCth[] = { DominioSituacaoConta.A, DominioSituacaoConta.F };

				// verifica existência de conta
				List<VFatContasHospPacientes> contasPac = getFaturamentoFacade().listarContasPorPacCodigoDthrRealizadoESituacaoCth(
					entidade.getPaciente().getCodigo(), entidade.getDthrRealizado(), dominiosSitCth,
					VFatContasHospPacientes.Fields.CTH_DT_INT_ADMINISTRATIVA.toString(), false);
				
				if(contasPac != null && !contasPac.isEmpty()) {
					// Deverá retornar apenas um registro
					VFatContasHospPacientes contaPac = contasPac.get(0);
					
					// Insere itens conta
					final FatItemContaHospitalar itemCth = new FatItemContaHospitalar();
					itemCth.setContaHospitalar(contaPac.getContaHospitalar());
					itemCth.setIchType(DominioItemConsultoriaSumarios.ICH);
					itemCth.setProcedimentoHospitalarInterno(entidade.getProcedimentoHospitalarInterno());
					itemCth.setValor(BigDecimal.ZERO);
					itemCth.setIndSituacao(DominioSituacaoItenConta.A);
					itemCth.setQuantidadeRealizada(entidade.getQuantidade());
					itemCth.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
					itemCth.setLocalCobranca(DominioLocalCobranca.I);
					itemCth.setDthrRealizado(entidade.getDthrRealizado());
					itemCth.setUnidadesFuncional(entidade.getUnidadeFuncional());
					itemCth.setProcedimentoAmbRealizado(entidade);
					
					getFaturamentoFacade().persistirItemContaHospitalar(itemCth, null, true, servidorLogado, new Date());
					
					// Insere cid na conta
					List<MbcDiagnosticoDescricao> listaDiagnosticoDescricao =  getBlocoCirurgicoFacade().listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(
							entidade.getProcEspPorCirurgia().getCirurgia().getSeq(), DominioClassificacaoDiagnostico.POS);
					
					Integer vCidSeq = 0;
					if (!listaDiagnosticoDescricao.isEmpty()) {
						vCidSeq = listaDiagnosticoDescricao.get(0).getId().getCidSeq();
					}
					if (!vCidSeq.equals(0)) {
						
						// Insere CID conta
						final FatCidContaHospitalar cidConta = new FatCidContaHospitalar();
						cidConta.setId(new FatCidContaHospitalarId(contaPac.getCthSeq(), vCidSeq, DominioPrioridadeCid.P));

						getCidContaHospitalarON().persistirCidContaHospitalar(cidConta, nomeMicrocomputador, dataFimVinculoServidor);
					}
				} 
				else { // Não encontrou conta deve inserir nova
					// v_seq_dcs := FATC_GERA_CONTA_SEM_INT
					final FatDadosContaSemInt contaSemInt = getFaturamentoFacade().fatcGeraContaSemInt( entidade.getEspecialidade(), 
						entidade.getDthrRealizado(), entidade.getPaciente(), entidade.getUnidadeFuncional(), nomeMicrocomputador, dataFimVinculoServidor);
					
					// Busca conta
					final FatContasHospitalares contaOld = getFatContasHospitalaresDAO().obterFatContaHospitalar(contaSemInt.getCthSeq());
					final FatContasHospitalares conta = getFatContasHospitalaresDAO().obterFatContaHospitalar(contaSemInt.getCthSeq());
					conta.setEspecialidade(entidade.getEspecialidade());
					conta.setProcedimentoHospitalarInterno(entidade.getProcedimentoHospitalarInterno());
					
					// Atualiza a conta
					getFaturamentoFacade().persistirContaHospitalar(conta, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
					
					// Insere itens conta
					final FatItemContaHospitalar itemCth = new FatItemContaHospitalar();
					itemCth.setId(new FatItemContaHospitalarId(contaSemInt.getCthSeq(), null));
					itemCth.setContaHospitalar(conta);
					itemCth.setIchType(DominioItemConsultoriaSumarios.ICH);
					itemCth.setProcedimentoHospitalarInterno(entidade.getProcedimentoHospitalarInterno());
					itemCth.setValor(BigDecimal.ZERO);
					itemCth.setIndSituacao(DominioSituacaoItenConta.A);
					itemCth.setQuantidadeRealizada(entidade.getQuantidade());
					itemCth.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
					itemCth.setLocalCobranca(DominioLocalCobranca.I);
					itemCth.setDthrRealizado(entidade.getDthrRealizado());
					itemCth.setUnidadesFuncional(entidade.getUnidadeFuncional());
					itemCth.setProcedimentoAmbRealizado(entidade);
					
					getFaturamentoFacade().persistirItemContaHospitalar(itemCth, null, true,servidorLogado, new Date());
					
					// Insere cid na conta
					List<MbcDiagnosticoDescricao> listaDiagnosticoDescricao =  getBlocoCirurgicoFacade().listarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(
							entidade.getProcEspPorCirurgia().getCirurgia().getSeq(), DominioClassificacaoDiagnostico.POS);
					
					Integer vCidSeq = 0;
					if (!listaDiagnosticoDescricao.isEmpty()) {
						vCidSeq = listaDiagnosticoDescricao.get(0).getId().getCidSeq();
					}
					if (!vCidSeq.equals(0)) {
						
						// Insere CID conta
						final FatCidContaHospitalar cidConta = new FatCidContaHospitalar();
						cidConta.setId(new FatCidContaHospitalarId(conta.getCthSeq(), vCidSeq, DominioPrioridadeCid.P));

						getCidContaHospitalarON().persistirCidContaHospitalar(cidConta, nomeMicrocomputador, dataFimVinculoServidor);
					}
				}
		}
	}
	

	/**
	 *  Atualiza a competência do procedimento
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarCompetencia(FatProcedAmbRealizado entidade) throws ApplicationBusinessException {
		
		if (entidade.getFatCompetencia() == null) {
			List<FatCompetencia> competencias = getFatCompetenciaDAO().obterCompetenciasPorModuloESituacaoEFaturado(
							DominioModuloCompetencia.AMB,
							DominioSituacaoCompetencia.A, false);

			if (competencias == null || competencias.size() == 0) {
				throw new ApplicationBusinessException(ProcedimentosAmbRealizadoRNExceptionCode.FAT_00453);
			} else {
				FatCompetencia competencia = competencias.get(0);
				entidade.setFatCompetencia(competencia);
			}
		}
	}
	
	/**
	 * Atualiza o servidor que está inserindo o procedimento ambulatorial
	 * @throws ApplicationBusinessException  
	 * 
	 */
	protected void atualizarServidor(FatProcedAmbRealizado entidade, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		if(entidade.getServidor() == null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			entidade.setServidor(servidorLogado);
		}
	}
	
	/**
	 * Atualiza os dados referentes a criação da linha (criado_em, criado_por,
	 * etc)
	 * 
	 */
	protected void atualizarDadosCriacao(FatProcedAmbRealizado entidade) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setCriadoEm(new Date());
		entidade.setAlteradoEm(new Date());
		
		if(entidade.getCriadoPor() == null) {
			entidade.setCriadoPor(servidorLogado.getUsuario());
		}
		
		if(entidade.getAlteradoPor() == null) {
			entidade.setAlteradoPor(servidorLogado.getUsuario());
		}
	}
	
	
	/**
	 * ORADB Trigger FATT_PMR_BASE_BRU
	 * ORADB Trigger FATT_PMR_BRU
	 * @param dataFimVinculoServidor 
	 */
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean bruPreAtualizacaoRow(FatProcedAmbRealizado original, FatProcedAmbRealizado modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		

//		DominioModuloCompetencia vModuloNovo = DominioModuloCompetencia.AMB;
//		Integer vMesNovo;
//		Integer vAnoNovo;
//		Date vDtHrInicioNovo;
		DominioSituacaoCompetencia vSituacao;
//		Date vDtRealizado = null;
//		Boolean vOk;		
		
		//--- se nao estiver executando agrupamento de consultas
		//--- e o item estiver sendo cancelado, so permite na Compt aberta
		//-- Milena 2/08/2005. Não permitir abertura fora da competência
		//-- ELSIF --:new.ind_situacao = 'C' AND
		if ((modificada.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.ABERTO) ||
			modificada.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO)) &&
			!modificada.getSituacao().equals(original.getSituacao()) && 
			!getFaturamentoFatkInterfaceAacRN().obterAtributoExecAgrupamento()) {
			//  -- cancelando ou abrindo o item, so permite se a competencia nao estiver faturada
			//  -- CFV(02/04/2001) Nova consistencia em situacao. Milena 08/08/2005
			// -- incluí teste para "A"bertura de procedimento também.
			//  -- da competencia
			if (!getFaturamentoFatkCapRN().obterAtributoCapEncerramento() &&
				!getFaturamentoFatkEpsRN().obterAtributoEpsEncerramento()) {
			    //-- tem que passar as datas porque quando se está abrindo - exames -
				//-- se altera a data para a nova competência e é permitido
				vSituacao = getFaturamentoFatkCpeRN().rnCpecVerSituacao(
						modificada.getFatCompetencia().getId().getModulo(),
						modificada.getFatCompetencia().getId().getDtHrInicio(),
						modificada.getFatCompetencia().getId().getMes(),
						modificada.getFatCompetencia().getId().getAno());
				if (DominioSituacaoCompetencia.P.equals(vSituacao)) {
					throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00512);
				}
			}
		}
		
		if (modificada.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO)) {
			//-- VERIFICA SE O CONVENIO E PLANO SAO VALIDOS PARA ESSE PROCEDIMENTO
			getContaHospitalarRN().verificarFiltroConvPlano(
					null, 
					null, 
					modificada.getSeq(), 
					modificada.getConvenioSaudePlano().getId().getCnvCodigo(), 
					modificada.getConvenioSaudePlano().getId().getSeq(), 
					modificada.getProcedimentoHospitalarInterno().getSeq(),
					null,
					null, 
					modificada.getIndOrigem().toString());
		}
		
		getFaturamentoFatkPmrRN().rnPmrpVerUniPint(modificada.getProcedimentoHospitalarInterno().getSeq(), 
				modificada.getConsultaProcedHospitalar() == null ? null : modificada.getConsultaProcedHospitalar().getId().getPhiSeq());
		
		modificada.setAlteradoEm(new Date());
		modificada.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		// -- Milena 13/outubro/2004
		if (modificada.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO)){
			getFaturamentoFatkPmrRN().rnPmrpAtuOtorrino(modificada.getSeq(), dataFimVinculoServidor);
		}
		
		//-- VER INTEGRIDADE COM AEL_ITEM_SOLICITACAO_EXAMES
		if (!CoreUtil.igual(original.getItemSolicitacaoExame(), modificada.getItemSolicitacaoExame()) &&
			modificada.getItemSolicitacaoExame() != null) {
			getItemContaHospitalarRN().fatpVerItemSolic(
					modificada.getItemSolicitacaoExame().getId().getSoeSeq(),
					modificada.getItemSolicitacaoExame().getId().getSeqp());
		}

		return super.bruPreAtualizacaoRow(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_PMR_BRD</code> <br/>
	 * </p>
	 */
	@Override
	public boolean brdPreRemocaoRow(final FatProcedAmbRealizado entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		// AghuFacade aghuFacade = getAghuFacade();
	
		// if
		// (!usuarioLogado.equals(aghuFacade.obterAghParametro(AghuParametrosEnum.P_NOME_USUARIO_BACKUP).getVlrTexto()))
		// {
		if (DominioSituacaoProcedimentoAmbulatorio.ENCERRADO.equals(entidade.getSituacao())
				|| DominioSituacaoProcedimentoAmbulatorio.APRESENTADO.equals(entidade.getSituacao())) {
			throw new ApplicationBusinessException(ProcedimentosAmbRealizadoRNExceptionCode.FATT_PMR_BRD_JA_PROCESSADO,
					entidade.getProcedimentoHospitalarInterno().getDescricao());
		}
		// }
		return super.brdPreRemocaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * <p>
	 * ORADB: <code>FATT_PMR_BSU</code> <br/>
	 * </p>
	 */
	@Override
	public boolean bsuPreAtualizacaoStatement(FatProcedAmbRealizado original, FatProcedAmbRealizado modificada) throws BaseException {
	
//		CREATE OR REPLACE TRIGGER "AGH"."FATT_PMR_BSU" BEFORE
//		  UPDATE
//		    ON FAT_PROCED_AMB_REALIZADOS DECLARE BEGIN
//		    /* QMS$CIRCUMVENT_MUTATING_TABLE_RESTRICTION */
//		    BEGIN fatK_pmr.init_pmr_stack;
//		END;
//		END;
//		/
//		getFaturamentoFatkPmrRN().processPmrUpdate(modificada, original);
		return super.bsuPreAtualizacaoStatement(original, modificada);
	}
	
	
	/**
	 * Atualiza um registro <br>
	 * na tabela FAT_PROCED_AMB_REALIZADOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(FatProcedAmbRealizado elemento) throws BaseException {
		this.getFatProcedAmbRealizadoDAO().atualizar(elemento);
	}
	
	
	
	/**
	 * Remove registros na <br>
	 * tabela FAT_PROCED_AMB_REALIZADOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void remover(FatProcedAmbRealizado elemento) throws BaseException {
		this.getFatProcedAmbRealizadoDAO().remover(elemento);
	}
	
	
	/** GET **/
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}
	
	protected FatProcedAmbRealizadoJnDAO getFatProcedAmbRealizadoJnDAO(){
		return fatProcedAmbRealizadoJnDAO;
	}

	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	protected FaturamentoFatkPmrRN getFaturamentoFatkPmrRN() {
		return faturamentoFatkPmrRN;
	}

	protected FaturamentoFatkPhiRN getFaturamentoFatkPhiRN() {
		return faturamentoFatkPhiRN;
	}
	
	protected ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	protected CidContaHospitalarON getCidContaHospitalarON(){
		return cidContaHospitalarON;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {		
		return cadastroPacienteFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected ItemContaHospitalarRN getItemContaHospitalarRN() {
		return itemContaHospitalarRN;

	}

	protected FaturamentoFatkInterfaceAacRN getFaturamentoFatkInterfaceAacRN() {
		return faturamentoFatkInterfaceAacRN;
	}

	protected FaturamentoFatkCapRN getFaturamentoFatkCapRN() {
		return faturamentoFatkCapRN;
	}

	protected FaturamentoFatkEpsRN getFaturamentoFatkEpsRN() {
		return faturamentoFatkEpsRN;
	}

	protected FaturamentoFatkCpeRN getFaturamentoFatkCpeRN() {
		return faturamentoFatkCpeRN;
	}
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}
	
	public AghParametros buscarAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.parametroFacade.buscarAghParametro(parametrosEnum);
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}