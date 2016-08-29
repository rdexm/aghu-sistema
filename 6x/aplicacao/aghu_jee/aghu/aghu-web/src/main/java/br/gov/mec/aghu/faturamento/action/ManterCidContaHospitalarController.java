package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCidContaHospitalarController extends ActionController {

	private static final long serialVersionUID = -176978487508442409L;
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}
		
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	/* FR @Out(required=false)
	      @In(required=false)*/	
	private AghCid cid;
	
	private FatCidContaHospitalar fatCidContaHospitalar;

	private VFatContaHospitalarPac fatContaHospitalarPacView;

	private Integer seq;
	
	//private FatCidContaHospitalar cidCtaHosp;
	
	private Integer cidSeq;
	private DominioPrioridadeCid prioridadeCid;	
	private Integer phiSeq;
	
	private static final Log LOG = LogFactory.getLog(ManterCidContaHospitalarController.class);

	public enum ManterCidContaHospitalarControllerExceptionCode implements
	BusinessExceptionCode {
		CID_CONTA_HOSPITALAR_CADASTRADO_SUCESSO,
		CID_CONTA_HOSPITALAR_EXCLUIDO_SUCESSO,
		CID_CONTA_HOSPITALAR_NAO_COMPATIVEL_SSM,
		CID_CONTA_HOSPITALAR_CADASTRADO_SEM_SUCESSO;
	}

	public void inicio() {
	 

		if(seq != null || ( fatContaHospitalarPacView != null && !fatContaHospitalarPacView.getSeq().equals(seq))) {			
			fatContaHospitalarPacView = faturamentoFacade.obterContaHospitalarPaciente(seq);
			
			if(fatContaHospitalarPacView != null && fatContaHospitalarPacView.getProcedimentoHospitalarInternoRealizado() != null){
				this.phiSeq = fatContaHospitalarPacView.getProcedimentoHospitalarInternoRealizado().getSeq();
			}
			FatCidContaHospitalarId id = new FatCidContaHospitalarId(fatContaHospitalarPacView.getCthSeq(), null, null);
			fatCidContaHospitalar = new FatCidContaHospitalar();
			fatCidContaHospitalar.setId(id);
			cid = null;
		}
	
	}
	
	/**
	 * Método para pesquisar CIDs na suggestion da tela. São pesquisados somente
	 * os 300 primeiros registros.
	 * 
	 * @param param
	 * @return Lista de objetos AghCid
	 */
	public List<AghCid> pesquisarCids(String param) {
		Integer phiSeqSG = null;
		if (DominioPrioridadeCid.P.equals(fatCidContaHospitalar.getId().getPrioridadeCid())){
			phiSeqSG = phiSeq;
		}
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorSSMDescricaoOuCodigo(phiSeqSG, param,
				Integer.valueOf(300)),pesquisarCidsCount(param));
	}
	
	public Long pesquisarCidsCount(String param) {
		Integer phiSeqSG = null;
		if (DominioPrioridadeCid.P.equals(fatCidContaHospitalar.getId().getPrioridadeCid())){
			phiSeqSG = phiSeq;
		}
		return faturamentoFacade.pesquisarCidsPorSSMDescricaoOuCodigoCount(phiSeqSG, param);
	}

	public boolean validarCidSelecionado(){
		if (DominioPrioridadeCid.P.equals(fatCidContaHospitalar.getId().getPrioridadeCid()) && cid != null){
			List<AghCid> listCids = faturamentoFacade.pesquisarCidsPorSSMDescricaoOuCodigo(phiSeq, cid.getCodigo(),
					Integer.valueOf(1));
			if(listCids == null || listCids.isEmpty()){
				apresentarMsgNegocio(Severity.ERROR,
						ManterCidContaHospitalarControllerExceptionCode.CID_CONTA_HOSPITALAR_NAO_COMPATIVEL_SSM.toString());
				return false;
			}
		}
		return true;
	}
	
	public void removerCid() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = this.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			LOG.info("Exceção caputada:", e);
		}
		try {
			final Date dataFimVinculoServidor = new Date();
			//RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), dataFimVinculoServidor);
			faturamentoFacade.removerCidContaHospitalar(seq, cidSeq, prioridadeCid, nomeMicrocomputador, dataFimVinculoServidor);
			//faturamentoFacade.flush();
			//this.reiniciarPaginator(LancarItensContaHospitalarPaginatorController.class);
			//this.reiniciarPaginator(ManterCidContaHospitalarPaginatorController.class);
			apresentarMsgNegocio(Severity.INFO, ManterCidContaHospitalarControllerExceptionCode.CID_CONTA_HOSPITALAR_EXCLUIDO_SUCESSO.toString());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String gravar() {
		if(validarCidSelecionado()){
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = this.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				LOG.info("Exceção caputada:", e);
			}
			try{
				//if (fatCidContaHospitalar.getId().getPrioridadeCid().equals(DominioPrioridadeCid.P)) //se ta inserindo um principal valida
				//	faturamentoFacade.buscaCountQtdCids(fatCidContaHospitalar.getId().getCthSeq(), fatCidContaHospitalar.getId().getPrioridadeCid());
				faturamentoFacade.buscaCountQtdCids(fatCidContaHospitalar.getId().getCthSeq(), cid.getSeq());
				
				fatCidContaHospitalar.setCid(cid);
				fatCidContaHospitalar.getId().setCidSeq(cid.getSeq());
	
				final Date dataFimVinculoServidor = new Date();			
						
				faturamentoFacade.persistirCidContaHospitalar(fatCidContaHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
				 
				//this.reiniciarPaginator(ManterCidContaHospitalarPaginatorController.class);
	
				apresentarMsgNegocio(Severity.INFO,
						ManterCidContaHospitalarControllerExceptionCode.CID_CONTA_HOSPITALAR_CADASTRADO_SUCESSO.toString());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		}else {
			return null;
		}
		return "lancarItensContaHospitalarList";
	}
	
	public String cancelar() {	
		return "lancarItensContaHospitalarList";		
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public FatCidContaHospitalar getFatCidContaHospitalar() {
		return fatCidContaHospitalar;
	}

	public void setFatCidContaHospitalar(FatCidContaHospitalar fatCidContaHospitalar) {
		this.fatCidContaHospitalar = fatCidContaHospitalar;
	}

	public VFatContaHospitalarPac getFatContaHospitalarPacView() {
		return fatContaHospitalarPacView;
	}

	public void setFatContaHospitalarPacView(
			VFatContaHospitalarPac fatContaHospitalarPacView) {
		this.fatContaHospitalarPacView = fatContaHospitalarPacView;
	}
	

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public DominioPrioridadeCid getPrioridadeCid() {
		return prioridadeCid;
	}

	public void setPrioridadeCid(DominioPrioridadeCid prioridadeCid) {
		this.prioridadeCid = prioridadeCid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public AghCid getCid() {
		return cid;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
}