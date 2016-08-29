package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospIntCidId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class RelacionarCidComProcedimentoHospitalarInternoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3385028558389031657L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private final String REDIRECIONA_CONSULTAR_PHI = "consultarPhiList";
	
	private boolean exibirModal;
	
	private boolean ativo;
	
	//Suggestion
	private FatProcedHospInternos procedHospInternos;
	
	//Lista de procedimento hospitalar
	private List<FatProcedHospIntCid> lista = null;
	
	//Para Adicionar itens
	private FatProcedHospIntCid fatProcedHospIntCid;
	
	private Integer phiSeq;
	
	//excluir
	private Integer phiSeqExcluir;
	private Integer cidSeqExcluir;

	public enum RelacionarCidComProcedimentoHospitalarInternoControllerExceptionCode implements
	BusinessExceptionCode {
		MSG_CID_JA_RELACIONADO_COM_PROCED_HOSPITALAR_INTERNO,
		MSG_GRAVADO_SUCESSO,
		MSG_CAMPO_PROCEDIMENTO_DEVE_SER_PREENCHIDO
		//O CID já está relacionado com este procedimento hospitalar interno.
	}

	@PostConstruct
	public void iniciar() {
		begin(conversation);
	
		ativo = false;
		
		inicializaFatCompatExclusItem();
		lista = new ArrayList<FatProcedHospIntCid>();
		
		//no caso de vir da tela de consultar phis, carrega os filtro e a lista.
		if (this.phiSeq != null){
			lista = faturamentoFacade.pesquisarFatProcedHospIntCidPorPhiSeq(this.phiSeq);
			ativo = true;
			List<FatProcedHospInternos> listaAux = faturamentoFacade.listarProcedHospInternoPorSeqOuDescricao((Object) this.phiSeq, 100,
					FatProcedHospInternos.Fields.SEQ.toString());
			 if(listaAux != null && ! listaAux.isEmpty()){
				 procedHospInternos = listaAux.get(0);
			 }
			}
	}
	
	/**
	 * Método para pesquisar FatProcedHospInternos na suggestion da tela
	 * 
	 * @return
	 */
	public List<FatProcedHospInternos> listarProcedHospInternosPorPhiSeqOuDescricao(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedHospInternoPorSeqOuDescricao(param, 100,
				FatProcedHospInternos.Fields.SEQ.toString()),listarProcedHospInternosPorPhiSeqOuDescricaoCount(param));
	}
	
	public Long listarProcedHospInternosPorPhiSeqOuDescricaoCount(String param) {
		return faturamentoFacade.listarProcedHospInternoPorSeqOuDescricaoCount(param);
	}

	/**
	 * Método para pesquisar CIDs na suggestion da tela
	 * 
	 * @param param
	 * @return Lista de objetos AghCid
	 */
	public List<AghCid> pesquisarCids(String param) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorDescricaoOuCodigo(param == null ? null : param, 100, AghCid.Fields.DESCRICAO.toString()),pesquisarCidsCount(param));
	}
	
	public Long pesquisarCidsCount(String param) {
		return faturamentoFacade.pesquisarCidsPorDescricaoOuCodigoCount(param == null ? null : param);
	}	
	
	/** Método usado no botão pesquisar
	 * 
	 * @return
	 */
	public String pesquisar() {
		try {
			if (procedHospInternos == null) {
				throw new ApplicationBusinessException(RelacionarCidComProcedimentoHospitalarInternoControllerExceptionCode.MSG_CAMPO_PROCEDIMENTO_DEVE_SER_PREENCHIDO);
			}
			
			lista = faturamentoFacade.pesquisarFatProcedHospIntCidPorPhiSeq(procedHospInternos.getSeq());
			ativo = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String limparPesquisa() {
		ativo = false;
		exibirModal = false;
		procedHospInternos = null;
		
		if (this.phiSeq != null) {
			return "consultarPHI";
		}
		return null;
	}
	
	public String gravar() {
		try {
			FatProcedHospIntCidId id = new FatProcedHospIntCidId(procedHospInternos.getSeq(), fatProcedHospIntCid.getCid().getSeq());
			FatProcedHospIntCid existe = faturamentoFacade.obterFatProcedHospIntCidPorChavePrimaria(id);
			if (existe != null) {
				throw new ApplicationBusinessException(RelacionarCidComProcedimentoHospitalarInternoControllerExceptionCode.MSG_CID_JA_RELACIONADO_COM_PROCED_HOSPITALAR_INTERNO);
			}
			fatProcedHospIntCid.setId(id);
			fatProcedHospIntCid.setProcedimentoHospitalarInterno(procedHospInternos);
			faturamentoFacade.persistirFatProcedHospIntCid(fatProcedHospIntCid, true);
			pesquisar();
			inicializaFatCompatExclusItem();
			
			apresentarMsgNegocio(RelacionarCidComProcedimentoHospitalarInternoControllerExceptionCode.MSG_GRAVADO_SUCESSO.toString());
		} 
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (this.phiSeq != null) {
			return REDIRECIONA_CONSULTAR_PHI;
		} else {
			return null;
		}
	}
	
		
	private void inicializaFatCompatExclusItem() {
		fatProcedHospIntCid = new FatProcedHospIntCid();
		fatProcedHospIntCid.setId(new FatProcedHospIntCidId());
		fatProcedHospIntCid.setPrincipal(true);
	}
	
	/** Retorna os tipos de transplante para o dropDown
	 * 
	 * @return
	 */
	public List<FatTipoTransplante> getTipoTransplanteItens() {
		return faturamentoFacade.listarTodosOsTiposTransplante();
	}

	public String remover() {
		try {
			faturamentoFacade.removerFatProcedHospIntCid(phiSeqExcluir, cidSeqExcluir, true);
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	/************************ GETTERS AND SETTERS *************************/
	public boolean isExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public List<FatProcedHospIntCid> getLista() {
		return lista;
	}

	public void setLista(List<FatProcedHospIntCid> lista) {
		this.lista = lista;
	}

	public FatProcedHospIntCid getFatProcedHospIntCid() {
		return fatProcedHospIntCid;
	}

	public void setFatProcedHospIntCid(FatProcedHospIntCid fatProcedHospIntCid) {
		this.fatProcedHospIntCid = fatProcedHospIntCid;
	}

	public FatProcedHospInternos getProcedHospInternos() {
		return procedHospInternos;
	}

	public void setProcedHospInternos(FatProcedHospInternos procedHospInternos) {
		this.procedHospInternos = procedHospInternos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getPhiSeqExcluir() {
		return phiSeqExcluir;
	}

	public void setPhiSeqExcluir(Integer phiSeqExcluir) {
		this.phiSeqExcluir = phiSeqExcluir;
	}

	public Integer getCidSeqExcluir() {
		return cidSeqExcluir;
	}

	public void setCidSeqExcluir(Integer cidSeqExcluir) {
		this.cidSeqExcluir = cidSeqExcluir;
	}


}
