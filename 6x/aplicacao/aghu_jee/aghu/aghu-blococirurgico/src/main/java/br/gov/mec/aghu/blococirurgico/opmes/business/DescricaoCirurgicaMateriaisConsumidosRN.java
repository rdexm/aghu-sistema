package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacaoMaterialItem;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class DescricaoCirurgicaMateriaisConsumidosRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaMateriaisConsumidosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;

	@EJB
	private IBlocoCirurgicoFacade IblocoCirurgicoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7547905525702158029L;
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO() {
		return mbcRequisicaoOpmesDAO;
	}
	
	protected MbcItensRequisicaoOpmesDAO getMbcItensRequisicaoOpmesDAO() {
		return mbcItensRequisicaoOpmesDAO;
	}
	
	protected MbcMateriaisItemOpmesDAO getMbcMateriaisItemOpmesDAO() {
		return mbcMateriaisItemOpmesDAO;
	}
	
	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.IblocoCirurgicoFacade;
	}

	// RN03 - #31998 - U03_JUST_MAT_CONS
	public void concluirJustificativa(Short seqRequisicao, String justificativa){
		
		if(seqRequisicao != null){
			MbcRequisicaoOpmes requisicaoOpme = this.getMbcRequisicaoOpmesDAO().obterPorChavePrimaria(seqRequisicao);
			requisicaoOpme.setJustificativaConsumoOpme(justificativa);
			
			this.getMbcRequisicaoOpmesDAO().atualizar(requisicaoOpme);
			this.getMbcRequisicaoOpmesDAO().flush();
		}
		
	}
	
	// RN04 - #31998
	public void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos, String justificativa) {
		this.getBlocoCirurgicoFacade().validarConcluirMateriaisConsumidos(listaMateriaisConsumidos);		
	}
	
	// RN06 - #31998
	public void concluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> itemLista, String usuarioLogado) {
		// #50594  ITEM 3 E 4
		for (DescricaoCirurgicaMateriaisConsumidosVO item : itemLista) {
			if(item.getSeqItemReq() !=  null){
				MbcItensRequisicaoOpmes itemRequisicao = mbcItensRequisicaoOpmesDAO.obterPorChavePrimaria(item.getSeqItemReq());
				this.atualizarItensRequisicao(item, itemRequisicao,usuarioLogado);
				this.atualizarMateriaisItem(item,itemRequisicao, usuarioLogado);
				// #50594  ITEM 6
			}else{
				 MbcItensRequisicaoOpmes itemReq = this.inserirItensRequisicao(item,usuarioLogado);
				 this.inserirMateriaisItensRequisicao(itemReq,usuarioLogado);
			}
		}
		// #50594  ITEM 6
		if(itemLista.size() > 0){
			if(itemLista.get(0).getSeqRequisicaoOpme() != null){
				MbcRequisicaoOpmes requisicaoOpme = this.getMbcRequisicaoOpmesDAO().obterPorChavePrimaria(itemLista.get(0).getSeqRequisicaoOpme());
				for (MbcItensRequisicaoOpmes item : requisicaoOpme.getItensRequisicao()) {
					if(DominioRequeridoItemRequisicao.NRQ.equals(item.getRequerido())){
						this.atualizarItensRequisicaoNRQ(item,usuarioLogado);
					}
				}
			}
		}
	}

	// #50594  ITEM 3 E 4
	private void atualizarMateriaisItem(DescricaoCirurgicaMateriaisConsumidosVO item,MbcItensRequisicaoOpmes itemRequisicao, String usuario){
		for (MbcMateriaisItemOpmes materialItem : itemRequisicao.getMateriaisItemOpmes()){
			materialItem.setQuantidadeConsumida(item.getQtdeUtilizada());
			this.getMbcMateriaisItemOpmesDAO().atualizar(materialItem);
		}
		this.getMbcMateriaisItemOpmesDAO().flush();
	}

	// #50594  ITEM 3 E 4
	private void atualizarItensRequisicao(DescricaoCirurgicaMateriaisConsumidosVO item,MbcItensRequisicaoOpmes itemRequisicao, String usuarioLogado){
		itemRequisicao = mbcItensRequisicaoOpmesDAO.obterPorChavePrimaria(itemRequisicao.getSeq());
		itemRequisicao.setQuantidadeConsumida(item.getQtdeUtilizada());
		itemRequisicao.setIndConsumido(Boolean.TRUE);
		Integer zero = 0;
		if(zero.equals(item.getQtdeUtilizada())){
			itemRequisicao.setIndConsumido(Boolean.FALSE);
		}
		this.getMbcItensRequisicaoOpmesDAO().atualizar(itemRequisicao);
		this.getMbcItensRequisicaoOpmesDAO().flush();
		
	}
	// #50594  ITEM 6
	private void atualizarItensRequisicaoNRQ(MbcItensRequisicaoOpmes itemRequisicao, String usuarioLogado){
		itemRequisicao = mbcItensRequisicaoOpmesDAO.obterPorChavePrimaria(itemRequisicao.getSeq());
		itemRequisicao.setQuantidadeConsumida(0);
		itemRequisicao.setQuantidadeAutorizadaSus((short)0);
		itemRequisicao.setQuantidadeSolicitada(0);
		itemRequisicao.setQuantidadeAutorizadaHospital(0);
		itemRequisicao.setIndConsumido(Boolean.TRUE);
		itemRequisicao.setRequerido(DominioRequeridoItemRequisicao.DSP);
		itemRequisicao.setIndAutorizado(false);
		itemRequisicao.setIndCompativel(false);
		this.getMbcItensRequisicaoOpmesDAO().atualizar(itemRequisicao);
		this.getMbcItensRequisicaoOpmesDAO().flush();
	}
	
	private MbcItensRequisicaoOpmes inserirItensRequisicao(DescricaoCirurgicaMateriaisConsumidosVO item, String usuarioLogado){
		MbcRequisicaoOpmes requisicao = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(item.getSeqRequisicaoOpme());
		MbcItensRequisicaoOpmes itemRequisicao = new MbcItensRequisicaoOpmes();
		itemRequisicao.setRequisicaoOpmes(requisicao);
		itemRequisicao.setQuantidadeConsumida(item.getQtdeUtilizada());
		itemRequisicao.setQuantidadeAutorizadaSus((short)0);
		itemRequisicao.setQuantidadeSolicitada(0);
		itemRequisicao.setQuantidadeAutorizadaHospital(0);
		itemRequisicao.setIndConsumido(Boolean.TRUE);
		itemRequisicao.setRequerido(DominioRequeridoItemRequisicao.DSP);
		itemRequisicao.setIndAutorizado(false);
		itemRequisicao.setIndCompativel(false);
		itemRequisicao.setCriadoEm(new Date());
		itemRequisicao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		this.getMbcItensRequisicaoOpmesDAO().persistir(itemRequisicao);
		this.getMbcItensRequisicaoOpmesDAO().flush();
		return itemRequisicao;
	}
	
	private void inserirMateriaisItensRequisicao(MbcItensRequisicaoOpmes item, String usuarioLogado){
		MbcMateriaisItemOpmes material = new MbcMateriaisItemOpmes();
		material.setItensRequisicaoOpmes(item);
		material.setSituacao(DominioSituacaoMaterialItem.A);
		material.setQuantidadeSolicitada(0);
		material.setQuantidadeConsumida(item.getQuantidadeConsumida());
		material.setCriadoEm(new Date());
		material.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		mbcMateriaisItemOpmesDAO.persistir(material);
		mbcMateriaisItemOpmesDAO.flush();
	}

	public void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido) {
		this.getBlocoCirurgicoFacade().validaQtdeUtilizada(itemMaterialConsumido);
	}

	public String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme) {
		return this.getBlocoCirurgicoFacade().montarJustificativaMateriaisConsumidos(seqRequisicaoOpme);
	}
	
}
