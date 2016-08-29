package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class LancarItensContaHospitalarController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(LancarItensContaHospitalarController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 9041577601091390580L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private LancarItensContaHospitalarPaginatorController lancarItensContaHospitalarPaginatorController;
	
	private FatItemContaHospitalar itemContaHospitalar;
	
	private FatItemContaHospitalar itemContaHospitalarClone;
	
	private VFatContaHospitalarPac contaHospitalarView;

	private FatContasHospitalares contaHospitalar;

	private VFatAssociacaoProcedimento procedimentoHospitalar;
	
	private AghUnidadesFuncionais unidadeFuncional;

	private RapServidores responsavel;
	
	private RapServidoresVO responsavelVO;
	
	private RapServidores anestesista;
	
	private RapServidoresVO anestesistaVO;
	
	private Integer cthSeq;
	
	private Short seq;
	
	private Short tipoCBO;
	
	private List<String> cboAnestesistas = new ArrayList<String>();
	
	public enum LancarItensContaHospitalarControllerExceptionCode implements
	BusinessExceptionCode {
		ITEM_CONTA_HOSPITALAR_ALTERADO_SUCESSO, ITEM_CONTA_HOSPITALAR_CADASTRADO_SUCESSO,
		ITEM_PROCEDIMENTO_REALIZADO_CONTA_NAO_PODE_SER_CANCELADO,
		SITUACAO_ITEM_NAO_PODE_SER_ALTERADA,
		ERRO_GENERICO_ITEM_CONTA_HOSPITALAR;
	}

	public void inicio() {
	 

		try {
			
			if(tipoCBO == null){
				AghParametros paramTipoCBO = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO);
				tipoCBO = paramTipoCBO.getVlrNumerico().shortValue();
			}
			
			if (cboAnestesistas.isEmpty()) {
				AghParametros pCboAnestesistas = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LISTA_CBO_ANESTESISTA);
				cboAnestesistas.addAll(Arrays.asList(pCboAnestesistas.getVlrTexto().split(",")));
			}
			
			if(cthSeq != null) {
				contaHospitalarView = faturamentoFacade.obterContaHospitalarPaciente(cthSeq);
				contaHospitalar = faturamentoFacade.obterContaHospitalar(cthSeq);
				if(seq != null) {
					itemContaHospitalar = faturamentoFacade.obterItemContaHospitalar(new FatItemContaHospitalarId(cthSeq, seq));
					
					//Clonando o item da conta hospitalar.
					itemContaHospitalarClone = faturamentoFacade.clonarItemContaHospitalar(itemContaHospitalar);
					
					if(itemContaHospitalar.getProcedimentoHospitalarInterno() != null) {
						List<VFatAssociacaoProcedimento> lista = ((List<VFatAssociacaoProcedimento>)this.listarProcedimentosSUSPorPHI(itemContaHospitalar.getProcedimentoHospitalarInterno().getSeq().toString()));
						procedimentoHospitalar =  lista.isEmpty() ? null : lista.get(0);
						//procedimentoHospitalar = ((List<VFatAssociacaoProcedimento>)this.listarProcedimentosSUSPorPHI(((FatConvGrupoItemProced)(itemContaHospitalar.getProcedimentoHospitalarInterno().getConvGrupoItensProced().toArray()[0])).getItemProcedHospitalar().getCodTabela())).get(0);
					}
					unidadeFuncional = itemContaHospitalar.getUnidadesFuncional();
					responsavel = itemContaHospitalar.getServidor();

					if(responsavel != null && responsavel.getPessoaFisica() != null){
						responsavelVO = registroColaboradorFacade.obterServidorVO(responsavel.getPessoaFisica().getCodigo(), 
																   responsavel.getId().getMatricula(), 
																   responsavel.getId().getVinCodigo(), 
																   tipoCBO);
					}

					anestesista = itemContaHospitalar.getServidorAnest();
					if(anestesista != null && anestesista.getPessoaFisica() != null){
						anestesistaVO = registroColaboradorFacade.obterServidorVO( anestesista.getPessoaFisica().getCodigo(), 
																	anestesista.getId().getMatricula(), 
																	anestesista.getId().getVinCodigo(), 
																	tipoCBO);
					}
					
				}
				else {
					itemContaHospitalar = new FatItemContaHospitalar();
					itemContaHospitalar.setId(new FatItemContaHospitalarId(cthSeq, null));
					itemContaHospitalar.setContaHospitalar(contaHospitalar);
					itemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.DIG);
					itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
					unidadeFuncional = null;
					responsavel = null;
					responsavelVO = null;
					anestesista = null;
					anestesistaVO = null;
					procedimentoHospitalar = null;
				}
			}
			else {
				contaHospitalarView = new VFatContaHospitalarPac();
				contaHospitalar = new FatContasHospitalares();
				itemContaHospitalar = new FatItemContaHospitalar();
			}
		} catch(Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.INFO,
					LancarItensContaHospitalarControllerExceptionCode.ERRO_GENERICO_ITEM_CONTA_HOSPITALAR.toString());			
		}
	
	}

	public List<VFatAssociacaoProcedimento> listarProcedimentosSUS(Object strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoSUSSolicitado(strPesquisa, null);
		} catch (BaseException e) {
			return new ArrayList<VFatAssociacaoProcedimento>();
		}
	}
	
	public Long listarProcedimentosSUSCount(Object strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoSUSSolicitadoCount(strPesquisa);
		} catch (BaseException e) {
			return 0l;
		}
	}

	public List<VFatAssociacaoProcedimento> listarProcedimentosSUSPorPHI(String strPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarAssociacaoProcedimentoSUSSolicitadoPorPHI(strPesquisa, null), 
					faturamentoFacade.listarAssociacaoProcedimentoSUSSolicitadoPorPHICount(strPesquisa));
		} catch (BaseException e) {
			return this.returnSGWithCount(new ArrayList<VFatAssociacaoProcedimento>(), 0);
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(strPesquisa), pesquisarUnidadeFuncionalCount(strPesquisa));
	}

	public Integer pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa).intValue();
	}

	public List<RapServidoresVO> pesquisarServidor(String strPesquisa) {
		List<String> cbos = buscarCbosPorIphSeqEPhoSeq();
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorPorCbo(strPesquisa, tipoCBO, cbos),
				registroColaboradorFacade.pesquisarServidorPorCboCount(strPesquisa, tipoCBO, cbos));
	}
	
	private List<String> buscarCbosPorIphSeqEPhoSeq() {
		List<String> cbos = new ArrayList<String>();
		
		if (procedimentoHospitalar != null) {
			List<FatProcedimentoCbo> pCbos = faturamentoFacade.listarProcedimentoCboPorIphSeqEPhoSeq(
					procedimentoHospitalar.getId().getIphSeq(), procedimentoHospitalar.getId().getIphPhoSeq());
			
			if (pCbos != null && !pCbos.isEmpty()){
				for (FatProcedimentoCbo fatProcedimentoCbo : pCbos) {
					cbos.add(fatProcedimentoCbo.getCbo().getCodigo());
				}
			}
		}
		return cbos;
	}
	
	public List<RapServidoresVO> pesquisarServidorAnestesista(String strPesquisa) {
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorPorCbo(strPesquisa, tipoCBO, cboAnestesistas),
				registroColaboradorFacade.pesquisarServidorPorCboCount(strPesquisa, tipoCBO, cboAnestesistas));
	}
	
	public void popularServidor(){
		if(responsavelVO != null ){
			responsavel = registroColaboradorFacade.obterServidor(new RapServidores(new RapServidoresId(responsavelVO.getMatricula(),responsavelVO.getVinculo())));
		} else {
			responsavel = null;
		}
	}
	
	public void popularAnestesista(){
		if(anestesistaVO != null){
			anestesista = registroColaboradorFacade.obterServidor(new RapServidores(new RapServidoresId(anestesistaVO.getMatricula(),anestesistaVO.getVinculo())));
		} else {
			anestesista = null;
		}
	}
	
	
	public String gravar() {
		try {
			if(procedimentoHospitalar != null) {
				itemContaHospitalar.setProcedimentoHospitalarInterno(procedimentoHospitalar.getProcedimentoHospitalarInterno());
			}
			else {
				itemContaHospitalar.setProcedimentoHospitalarInterno(null);
			}
			
			itemContaHospitalar.setUnidadesFuncional(unidadeFuncional);
			itemContaHospitalar.setServidor(responsavel);
			itemContaHospitalar.setServidorAnest(anestesista);
			
			if(seq == null) {
				itemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
				itemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
			}
			
			if(itemContaHospitalar.getProcedimentoHospitalarInterno() != null && contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null) {
				if(contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq().equals(itemContaHospitalar.getProcedimentoHospitalarInterno().getSeq()) 
						&& DominioSituacaoItenConta.C.equals(itemContaHospitalar.getIndSituacao())) {
					throw new ApplicationBusinessException(LancarItensContaHospitalarControllerExceptionCode.ITEM_PROCEDIMENTO_REALIZADO_CONTA_NAO_PODE_SER_CANCELADO);
				}
			}
			
			/* #51536
			 * if(itemContaHospitalar.getId().getSeq() != null && CoreUtil.modificados(itemContaHospitalar.getIndSituacao(), itemContaHospitalarClone.getIndSituacao())) {
				if(!(DominioIndOrigemItemContaHospitalar.DIG.equals(itemContaHospitalarClone.getIndOrigem()) 
						|| DominioIndOrigemItemContaHospitalar.BCC.equals(itemContaHospitalarClone.getIndOrigem())
						|| DominioIndOrigemItemContaHospitalar.MPM.equals(itemContaHospitalarClone.getIndOrigem())
						|| DominioIndOrigemItemContaHospitalar.AEL.equals(itemContaHospitalarClone.getIndOrigem())
						|| DominioIndOrigemItemContaHospitalar.MCO.equals(itemContaHospitalarClone.getIndOrigem())
						|| DominioIndOrigemItemContaHospitalar.ANU.equals(itemContaHospitalarClone.getIndOrigem())
				)) {
					throw new ApplicationBusinessException(LancarItensContaHospitalarControllerExceptionCode.SITUACAO_ITEM_NAO_PODE_SER_ALTERADA);
				}
			}*/
			
			faturamentoFacade.persistirItemContaHospitalar(itemContaHospitalar, itemContaHospitalarClone,  responsavel, new Date());
			
			if(seq != null) {
				apresentarMsgNegocio(Severity.INFO,
						LancarItensContaHospitalarControllerExceptionCode.ITEM_CONTA_HOSPITALAR_ALTERADO_SUCESSO.toString());
			}
			else {
				apresentarMsgNegocio(Severity.INFO,
						LancarItensContaHospitalarControllerExceptionCode.ITEM_CONTA_HOSPITALAR_CADASTRADO_SUCESSO.toString());				
			}
			seq = null;
			this.responsavelVO = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "lancarItensContaHospitalar";
		}
		lancarItensContaHospitalarPaginatorController.setGravouLancarItemContaHosp(true);
		return "lancarItensContaHospitalarList";	
	}
	
	public String cancelar() {	
		seq = null;
		responsavel = anestesista = null; 
		responsavelVO = anestesistaVO = null;
		return "lancarItensContaHospitalarList";		
	}
	
	public boolean isOrigemDigitada() {
		if (itemContaHospitalar != null) {
			return DominioIndOrigemItemContaHospitalar.DIG.equals(itemContaHospitalar.getIndOrigem()); 
		}
		
		return false;
	}

	public DominioSituacaoItenConta[] situacaoesItemConta() {
		return new DominioSituacaoItenConta[] {DominioSituacaoItenConta.A, DominioSituacaoItenConta.C};
	}

	public FatItemContaHospitalar getItemContaHospitalar() {
		return itemContaHospitalar;
	}

	public void setItemContaHospitalar(FatItemContaHospitalar itemContaHospitalar) {
		this.itemContaHospitalar = itemContaHospitalar;
	}

	public VFatContaHospitalarPac getContaHospitalarView() {
		return contaHospitalarView;
	}

	public void setContaHospitalarView(VFatContaHospitalarPac contaHospitalarView) {
		this.contaHospitalarView = contaHospitalarView;
	}

	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(
			VFatAssociacaoProcedimento procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public RapServidores getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(RapServidores anestesista) {
		this.anestesista = anestesista;
	}

	public Short getTipoCBO() {
		return tipoCBO;
	}

	public void setTipoCBO(Short tipoCBO) {
		this.tipoCBO = tipoCBO;
	}

	public RapServidoresVO getResponsavelVO() {
		return responsavelVO;
	}

	public void setResponsavelVO(RapServidoresVO responsavelVO) {
		this.responsavelVO = responsavelVO;
	}

	public RapServidoresVO getAnestesistaVO() {
		return anestesistaVO;
	}

	public void setAnestesistaVO(RapServidoresVO anestesistaVO) {
		this.anestesistaVO = anestesistaVO;
	}
}
