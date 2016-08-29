package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.SelectEvent;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.GrupoComponenteNPTVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaHistoricoGrupoComponentesNPTController extends ActionController {
	private static final long serialVersionUID = -1846518679437964237L;
	private List<AfaGrupoComponNptJn> listaHistorico;
	private GrupoComponenteNPTVO itemSelecionado;
	
	private Short seq;
	
	private static final String REDIRECIONA_COMPONENTES_NPT = "pesquisarGruposComponentesNPT";

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	public void inicio() {
		itemSelecionado = new GrupoComponenteNPTVO();
		pesquisarConsultorInternacao();
	}
	
	public void selecionarItem(SelectEvent e) {
		AfaGrupoComponNptJn item = (AfaGrupoComponNptJn) e.getObject();
		populaVO(item);
	}
	
	public void pesquisarConsultorInternacao() {
		listaHistorico = farmaciaFacade.pesquisarHistoricoGrupoComponenteNPT(seq);
		if (listaHistorico != null && listaHistorico.size() > 0) {
			populaVO(listaHistorico.get(0));
		}
	}
	
	public List<AfaGrupoComponNptJn> getListaHistorico() {
		return listaHistorico;
	}
	public void setListaHistorico(List<AfaGrupoComponNptJn> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}
	
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	public String cancelar() {
		return REDIRECIONA_COMPONENTES_NPT;
	}
	
	public GrupoComponenteNPTVO getItemSelecionado() {
		return itemSelecionado;
	}
	
	public void setItemSelecionado(GrupoComponenteNPTVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
	public void populaVO(AfaGrupoComponNptJn item){
		String atualizacao = "Atualização";
		String exclusao = "Exclusão";
		
		this.itemSelecionado.setCriadoEm(item.getCriadoEm());
		this.itemSelecionado.setDescricao(item.getDescricao());
		this.itemSelecionado.setMovimentacao(item.getDataAlteracao());
		this.itemSelecionado.setSeq(item.getSeq());
		this.itemSelecionado.setObservacao(item.getObservacao());
		this.itemSelecionado.setUsuario(item.getNomeUsuario());
		this.itemSelecionado.setSituacaoBoolean(false);
		
		if(DominioSituacao.A.equals(item.getIndSituacao())){
			this.itemSelecionado.setSituacaoBoolean(true);
		}
		
		if(DominioOperacoesJournal.UPD.equals(item.getOperacao())){
			this.itemSelecionado.setOperacao(atualizacao);
		}else if(DominioOperacoesJournal.DEL.equals(item.getOperacao())){
			this.itemSelecionado.setOperacao(exclusao);
		}
		RapServidores servidor = servidorLogadoFacade.obterServidorPorChavePrimaria(item.getSerMatricula(), item.getSerVinCodigo());
		this.itemSelecionado.setCriadoPor(servidor.getUsuario());
		
		RapServidores servidorUsuario = new RapServidores();
		try {
			servidorUsuario = registroColaboradorFacade.obterServidorPorUsuario(item.getNomeUsuario());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
		if(servidorUsuario.getPessoaFisica() != null){
			this.itemSelecionado.setResponsavel(servidorUsuario.getPessoaFisica().getNome());
		}
		
	}
}
