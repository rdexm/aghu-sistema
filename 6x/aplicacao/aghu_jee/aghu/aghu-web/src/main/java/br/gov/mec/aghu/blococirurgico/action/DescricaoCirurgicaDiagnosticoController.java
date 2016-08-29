package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricaoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class DescricaoCirurgicaDiagnosticoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = -7933867498851738285L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	private MbcDescricaoItens mbcDescricaoItem;

	private List<MbcDiagnosticoDescricao> listaPreOperatorio;
	
	private List<MbcDiagnosticoDescricao> listaPosOperatorio;

	private AghCid cidPre;
	
	private AghCid cidPos;
	
	private Integer dcgCrgSeqExc;
	private Short dcgSeqpExc;
	private Integer cidSeqExc;
	private DominioClassificacaoDiagnostico classificacaoExc;
	private boolean excluirPosOp;
	
	private Boolean exibeModalConfirmacaoExclusaoPreEPos = Boolean.FALSE;
	
	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	private DominioAsa asa;
	
	
	public void setarParametros(Integer cidSeqPre, Integer cidSeqPos){
		if(cidSeqPre != null){
			setCidPre(this.aghuFacade.obterCid(cidSeqPre));
		}
		
		if(cidSeqPos != null){
			setCidPos(this.aghuFacade.obterCid(cidSeqPos));
		}
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO){
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		
		this.mbcDescricaoItem = blocoCirurgicoFacade.obterMbcDescricaoItensPorId(new MbcDescricaoItensId( descricaoCirurgicaVO.getDcgCrgSeq(), 
				 																						  descricaoCirurgicaVO.getDcgSeqp()
				 																						)
																				);
		this.asa = this.mbcDescricaoItem.getAsa();
		atualizaListas();
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	private void atualizaListas(){
		listaPreOperatorio = blocoCirurgicoFacade.buscarMbcDiagnosticoDescricao(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp(), DominioClassificacaoDiagnostico.PRE);
		listaPosOperatorio = blocoCirurgicoFacade.buscarMbcDiagnosticoDescricao(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp(), DominioClassificacaoDiagnostico.POS);
	}
	
	public void excluir() {
		try {
			final MbcDiagnosticoDescricaoId id = new MbcDiagnosticoDescricaoId();
			
			id.setCidSeq(cidSeqExc);
			id.setDcgCrgSeq(dcgCrgSeqExc);
			id.setDcgSeqp(dcgSeqpExc);
			id.setClassificacao(classificacaoExc);
			
			Boolean exibirModal = false;
			
			if (DominioClassificacaoDiagnostico.PRE.equals(classificacaoExc)) {
				final MbcDiagnosticoDescricaoId idPos = new MbcDiagnosticoDescricaoId();
				idPos.setCidSeq(cidSeqExc);
				idPos.setDcgCrgSeq(dcgCrgSeqExc);
				idPos.setDcgSeqp(dcgSeqpExc);
				idPos.setClassificacao(DominioClassificacaoDiagnostico.POS);

				MbcDiagnosticoDescricao diagnosticoDescricaoPre = blocoCirurgicoFacade.obterDiagnosticoDescricaoPorChavePrimaria(id);
				MbcDiagnosticoDescricao diagnosticoDescricaoPos = blocoCirurgicoFacade.obterDiagnosticoDescricaoPorChavePrimaria(idPos);

				if(diagnosticoDescricaoPre != null && diagnosticoDescricaoPos != null) {
					exibirModal = true;
				}
				
				if (diagnosticoDescricaoPre != null) {
					blocoCirurgicoFacade.excluirDiagnosticoDescricoes(id);					
					diagnosticoDescricaoPre = null;
				}

				if (diagnosticoDescricaoPos != null && excluirPosOp) {
					blocoCirurgicoFacade.excluirDiagnosticoDescricoes(idPos);					
					excluirPosOp = false;
					diagnosticoDescricaoPos = null;
				}

				if(Boolean.FALSE.equals(excluirPosOp)) {
					super.closeDialog("modalConfirmacaoExclusaoPreEPosWG");
				}
				
				// Caso ainda não tenha excluído o diagnóstico do Pós-operatório e não está exibindo modal de exclusão sequencial (pré e pós)
				if (exibirModal) {
					super.openDialog("modalConfirmacaoExclusaoPreEPosWG");
					return;
				}
			} else {
				blocoCirurgicoFacade.excluirDiagnosticoDescricoes(id);				
			}
			
			//final AghCid cid = aghuFacade.obterAghCidPorChavePrimaria(cidSeqExc);
			//this.apresentarMsgNegocio(Severity.INFO, "CID_EXCLUSAO_SUCESSO", cid.getCodigo());
			atualizaListas();
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void salvarAsa(){
		try {
			mbcDescricaoItem = blocoCirurgicoFacade.obterMbcDescricaoItensPorId(new MbcDescricaoItensId( descricaoCirurgicaVO.getDcgCrgSeq(),descricaoCirurgicaVO.getDcgSeqp()));
			mbcDescricaoItem.setAsa(this.asa);
			blocoCirurgicoFacade.alterarMbcDescricaoItens(mbcDescricaoItem);
		
			//this.apresentarMsgNegocio(Severity.INFO,"DESCRICAO_ITEM_ALTERADO_SUCESSO");
			
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} 
	}
	
	public void atualizarCid(MbcDiagnosticoDescricao diagnosticoDescricao){
		MbcCirurgias cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoCirurgicaVO.getDcgCrgSeq());
		diagnosticoDescricao.setCirurgia(cirurgia);
		MbcDescricaoCirurgica mdc = this.blocoCirurgicoFacade.buscarDescricaoCirurgica(diagnosticoDescricao.getId().getDcgCrgSeq(), diagnosticoDescricao.getId().getDcgSeqp());
		diagnosticoDescricao.setMbcDescricaoCirurgica(mdc);
		try {
			final RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			diagnosticoDescricao.setServidor(servidorLogado);
			this.blocoCirurgicoFacade.alterarDiagnosticoDescricoes(diagnosticoDescricao);
			atualizaListas();
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void salvarCidPre(){
		try {
			
			final MbcDiagnosticoDescricao diagnosticoDescricao = new MbcDiagnosticoDescricao();
			
			diagnosticoDescricao.setId(new MbcDiagnosticoDescricaoId( descricaoCirurgicaVO.getDcgCrgSeq(), 
																	  descricaoCirurgicaVO.getDcgSeqp(), 
																	  cidPre.getSeq(), 
																	  DominioClassificacaoDiagnostico.PRE
																	)
									  );
			
			MbcCirurgias cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoCirurgicaVO.getDcgCrgSeq());
			
			final RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			diagnosticoDescricao.setServidor(servidorLogado);
			diagnosticoDescricao.setCid(cidPre);
			diagnosticoDescricao.setCirurgia(cirurgia);
			diagnosticoDescricao.setDestacar(Boolean.FALSE);
			
			blocoCirurgicoFacade.inserirDiagnosticoDescricoesPreOperatorio(diagnosticoDescricao, listaPreOperatorio, listaPosOperatorio);
			
			atualizaListas();
			//this.apresentarMsgNegocio(Severity.INFO,"CID_INSERCAO_SUCESSO", cidPre.getCodigo());
			
			cidPre = null;
			
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}

	public void salvarCidPos(){
		try {
			
			final MbcDiagnosticoDescricao diagnosticoDescricao = new MbcDiagnosticoDescricao();

			
			diagnosticoDescricao.setId(new MbcDiagnosticoDescricaoId( descricaoCirurgicaVO.getDcgCrgSeq(), 
																	  descricaoCirurgicaVO.getDcgSeqp(), 
																	  cidPos.getSeq(), 
																	  DominioClassificacaoDiagnostico.POS
																	)
									  );
			
			MbcCirurgias cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoCirurgicaVO.getDcgCrgSeq());
			
			final RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			diagnosticoDescricao.setServidor(servidorLogado);
			diagnosticoDescricao.setCid(cidPos);
			diagnosticoDescricao.setCirurgia(cirurgia);
			diagnosticoDescricao.setDestacar(Boolean.FALSE);
		
			blocoCirurgicoFacade.inserirDiagnosticoDescricoesPosOperatorio(diagnosticoDescricao,listaPreOperatorio, listaPosOperatorio);
			//this.apresentarMsgNegocio(Severity.INFO,"CID_INSERCAO_SUCESSO", cidPos.getCodigo());
			cidPos = null; 
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	/**
	 * Método para pesquisar CIDs na suggestion da tela. São pesquisados somente
	 * os 300 primeiros registros.
	 * 
	 * @param param
	 * @return Lista de objetos AghCids
	 */
	public List<AghCid> pesquisarCids(String param) {
		List<AghCid> cids = aghuFacade.pesquisarCidsPorDescricaoOuId(param,
				Integer.valueOf(300), true);
		
		return this.returnSGWithCount(cids, cids.size());
	}

	public String getTextoasa() {
		return "I.Saudável sem doença sistêmica.Paciente fora das extremidades de idade.\n" +
	 	  "II.Com doença acometendo um sistema,bem controlada,não afeta atividades diárias habituais. Outros riscos anestésicos como obesidade,alcoolismo,tabagismo podem ser incorporados neste nível.\n" +
	 	  "III.Com doença acometendo vários sistemas ou  doença bem controlada de sistema maior. Há limitação da atividade diária. Não há perigo imediato de vida.Diabete melitus severa, angina,infarto.\n" +
	 	  "IV.Com doença severa e incapacitante. Doença mal controlada ou em estágio final. Perigo de morte presente. Miocárdica com sinais evidentes de insuficiência cardíaca,angina persistente ou miocardite ativa;hepatopatia; nefropatia; insuficiência pulmonar ou endócrina.\n" +
	 	  "V.Com risco iminente de vida. Cirurgia como última possibilidade de manutenção da vida. Paciente com expectativa de vida abaixo de 24 horas. Aneurisma roto de aorta com choque;  trauma cerebral maior com aumento progressivo de pressão intracraniana; embolia pulmonar maciça.";
	}
	
	public String pesquisaCidCapitulo(){
		return PAGE_PESQUISA_CID;
	}

	public List<MbcDiagnosticoDescricao> getListaPreOperatorio() {
		return listaPreOperatorio;
	}

	public void setListaPreOperatorio(
			List<MbcDiagnosticoDescricao> listaPreOperatorio) {
		this.listaPreOperatorio = listaPreOperatorio;
	}

	public List<MbcDiagnosticoDescricao> getListaPosOperatorio() {
		return listaPosOperatorio;
	}

	public void setListaPosOperatorio(
			List<MbcDiagnosticoDescricao> listaPosOperatorio) {
		this.listaPosOperatorio = listaPosOperatorio;
	}

	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public Integer getDcgCrgSeqExc() {
		return dcgCrgSeqExc;
	}

	public void setDcgCrgSeqExc(Integer dcgCrgSeqExc) {
		this.dcgCrgSeqExc = dcgCrgSeqExc;
	}

	public Short getDcgSeqpExc() {
		return dcgSeqpExc;
	}

	public void setDcgSeqpExc(Short dcgSeqpExc) {
		this.dcgSeqpExc = dcgSeqpExc;
	}

	public Integer getCidSeqExc() {
		return cidSeqExc;
	}

	public void setCidSeqExc(Integer cidSeqExc) {
		this.cidSeqExc = cidSeqExc;
	}

	public DominioClassificacaoDiagnostico getClassificacaoExc() {
		return classificacaoExc;
	}

	public void setClassificacaoExc(DominioClassificacaoDiagnostico classificacaoExc) {
		this.classificacaoExc = classificacaoExc;
	}

	public AghCid getCidPre() {
		return cidPre;
	}

	public void setCidPre(AghCid cidPre) {
		this.cidPre = cidPre;
	}

	public AghCid getCidPos() {
		return cidPos;
	}

	public void setCidPos(AghCid cidPos) {
		this.cidPos = cidPos;
	}

	public MbcDescricaoItens getMbcDescricaoItem() {
		return mbcDescricaoItem;
	}

	public void setMbcDescricaoItem(MbcDescricaoItens mbcDescricaoItem) {
		this.mbcDescricaoItem = mbcDescricaoItem;
	}

	public boolean isExcluirPosOp() {
		return excluirPosOp;
	}

	public void setExcluirPosOp(boolean excluirPosOp) {
		this.excluirPosOp = excluirPosOp;
	}

	public Boolean getExibeModalConfirmacaoExclusaoPreEPos() {
		return exibeModalConfirmacaoExclusaoPreEPos;
	}

	public void setExibeModalConfirmacaoExclusaoPreEPos(
			Boolean exibeModalConfirmacaoExclusaoPreEPos) {
		this.exibeModalConfirmacaoExclusaoPreEPos = exibeModalConfirmacaoExclusaoPreEPos;
	}


	public DominioAsa getAsa() {
		return asa;
	}


	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}
		
	
}
