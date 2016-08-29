package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatMotivoDesdobrSsmVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinicaId;
import br.gov.mec.aghu.model.FatMotivoDesdobrSsm;
import br.gov.mec.aghu.model.FatMotivoDesdobrSsmId;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroMotivosDesdobramentoController extends ActionController {
	
	private static final long serialVersionUID = 4149386499612285872L;
	
	private static final String PAGE_PESQUISA_MOTIVOS_DESDOBRAMENTO = "faturamento-pesquisaMotivoDesdobramento";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private FatMotivoDesdobramento fatMotivoDesdobramento = new FatMotivoDesdobramento();
	
	private AghClinicas aghClinicas = null;
	
	private List<AghClinicas> listaClinicas = new ArrayList<AghClinicas>();
	
	private AghClinicas clinicaSelecionada;
	
	private FatItensProcedHospitalar fatItensProcedHospitalar = null;
	
	private List<FatMotivoDesdobrSsmVO> listaMotivosDesdobramentosSSM = new ArrayList<FatMotivoDesdobrSsmVO>();
	
	private FatMotivoDesdobrSsm fatMotivoDesdobrSsm = new FatMotivoDesdobrSsm();
	
	private FatMotivoDesdobrSsmVO fatMotivoDesdobrSsmVO;
	
	private FatTipoAih tipoAihSuggestion = null;
	
	private Boolean ativo;
	
	private Boolean ativoProcedimento;
	
	private Short diasInternacaoProcedimento;
	
	private boolean edicao;
	
	private boolean edicaoMotivoDesdobramentoSSM;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Método chamado na iniciação da tela.
	 */
	public void iniciar() {
	
		if (super.isValidInitMethod()) {
				
			aghClinicas = null;
			limparCamposAba2();
			
			if (edicao) {
				if (fatMotivoDesdobramento.getSituacaoRegistro().equals(DominioSituacao.A)) {
					ativo = true;
				} else {
					ativo = false;
				}
				
				tipoAihSuggestion = fatMotivoDesdobramento.getTipoAih();
				
				listaClinicas = faturamentoFacade.pesquisarClinicasPorMotivoDesdobramento(this.fatMotivoDesdobramento.getSeq());
				
				List<FatMotivoDesdobrSsm> listaFatMotivoDesdobrSsm = faturamentoFacade.pesquisarMotivosDesdobramentosSSM(this.fatMotivoDesdobramento.getSeq());
				 			 
				this.transformarEmListaDeVO(listaFatMotivoDesdobrSsm);
				
				edicaoMotivoDesdobramentoSSM = false;
				
			} else {
				listaClinicas = new ArrayList<AghClinicas>();
				listaMotivosDesdobramentosSSM = new ArrayList<FatMotivoDesdobrSsmVO>();
				tipoAihSuggestion = null;
				fatMotivoDesdobramento = new FatMotivoDesdobramento();
				ativo = true;
			}
		}
	}
		
	
	/**
	 * Transforma lista de entidade em lista de VO.
	 * 
	 * @param listaFatMotivoDesdobrSsm
	 */
	private void transformarEmListaDeVO(List<FatMotivoDesdobrSsm> listaFatMotivoDesdobrSsm) {
		
		listaMotivosDesdobramentosSSM = new ArrayList<FatMotivoDesdobrSsmVO>();
		 
		if (listaFatMotivoDesdobrSsm != null && !listaFatMotivoDesdobrSsm.isEmpty()) {
			FatMotivoDesdobrSsmVO fatMotivoDesdobrSsmVO;
			for (FatMotivoDesdobrSsm ssm: listaFatMotivoDesdobrSsm) {
				fatMotivoDesdobrSsmVO = new FatMotivoDesdobrSsmVO();
				fatMotivoDesdobrSsmVO.setFatMotivoDesdobrSsm(ssm);
				listaMotivosDesdobramentosSSM.add(fatMotivoDesdobrSsmVO);
			}
		}
	}
	
	/**
	 * Grava motivo de desdobramento.
	 * 
	 * @return String de navegação.
	 */
	public void gravar() {
		
		fatMotivoDesdobramento.setTipoAih(tipoAihSuggestion);
		
		boolean isEdicao;
		
		if (ativo) {
			fatMotivoDesdobramento.setSituacaoRegistro(DominioSituacao.A);
		} else {
			fatMotivoDesdobramento.setSituacaoRegistro(DominioSituacao.I);
		}
		
		//Se sequencial estiver preenchido, é edição.
		if (fatMotivoDesdobramento.getSeq() == null) {
			isEdicao = false;
		} else {
			isEdicao = true;
		}
		
		faturamentoFacade.persistirMotivoDesdobramento(fatMotivoDesdobramento);
		
		edicao = true;
		
		if (isEdicao) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_DESDOBRAMENTO", fatMotivoDesdobramento.getDescricao());
		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_MOTIVO_DESDOBRAMENTO", fatMotivoDesdobramento.getDescricao());
		}
				
	}
	
	/**
	 * Adiciona clínica ao motivo de desdobramento.
	 */
	public void adicionarClinica() {
		FatMotivoDesdobrClinicaId fatMotivoDesdobrClinicaId = new FatMotivoDesdobrClinicaId();
		fatMotivoDesdobrClinicaId.setMdsSeq(this.fatMotivoDesdobramento.getSeq());
		fatMotivoDesdobrClinicaId.setClcCodigo(this.aghClinicas.getCodigo());
		
		FatMotivoDesdobrClinica fatMotivoDesdobrClinica = new FatMotivoDesdobrClinica(fatMotivoDesdobrClinicaId);
		fatMotivoDesdobrClinica.setMotivoDesdobramento(this.fatMotivoDesdobramento);
		fatMotivoDesdobrClinica.setAghClinicas(this.aghClinicas);
		
		try {
			
			faturamentoFacade.persistirMotivoDesdobramentoClinica(fatMotivoDesdobrClinica);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_MOTIVO_DESDOBRAMENTO_CLINICA", fatMotivoDesdobrClinica.getAghClinicas().getDescricao());
			
			listaClinicas = faturamentoFacade.pesquisarClinicasPorMotivoDesdobramento(this.fatMotivoDesdobramento.getSeq());
			
			aghClinicas = null;
						
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	
	/**
	 * Adiciona procedimento ao motivo de desdobramento.
	 */
	public void adicionarProcedimento() {
		
		FatMotivoDesdobrSsmId fatMotivoDesdobrSsmId = new FatMotivoDesdobrSsmId();
		fatMotivoDesdobrSsmId.setIphSeq(fatItensProcedHospitalar.getId().getSeq());
		fatMotivoDesdobrSsmId.setIphPhoSeq(fatItensProcedHospitalar.getId().getPhoSeq());		
		fatMotivoDesdobrSsmId.setMdsSeq(fatMotivoDesdobramento.getSeq());
		
		fatMotivoDesdobrSsm.setId(fatMotivoDesdobrSsmId);
		fatMotivoDesdobrSsm.setDiasInternacao(this.diasInternacaoProcedimento);
		
		if (ativoProcedimento) {
			fatMotivoDesdobrSsm.setIndSituacao(DominioSituacao.A);
		} else {
			fatMotivoDesdobrSsm.setIndSituacao(DominioSituacao.I);
		}
		
		fatMotivoDesdobrSsm.setCriadoPor(super.obterLoginUsuarioLogado());
		fatMotivoDesdobrSsm.setAlteradoPor(super.obterLoginUsuarioLogado());
		fatMotivoDesdobrSsm.setCriadoEm(new Date());
		fatMotivoDesdobrSsm.setAlteradoEm(new Date());
		
		try {
			faturamentoFacade.persistirMotivoDesdobramentoProcedimento(fatMotivoDesdobrSsm);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_MOTIVO_DESDOBRAMENTO_SSM", fatItensProcedHospitalar.getDescricao());
			
			List<FatMotivoDesdobrSsm> listaFatMotivoDesdobrSsm = faturamentoFacade.pesquisarMotivosDesdobramentosSSM(this.fatMotivoDesdobramento.getSeq());
			
			this.transformarEmListaDeVO(listaFatMotivoDesdobrSsm);
			
			this.limparCamposAba2();			
			
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Exclui clínica de motivo desdobramento.
	 */
	public void excluirClinicaMotivoDesdobramento() {
		
		FatMotivoDesdobrClinicaId fatMotivoDesdobrClinicaId = new FatMotivoDesdobrClinicaId();
		fatMotivoDesdobrClinicaId.setClcCodigo(clinicaSelecionada.getCodigo());
		fatMotivoDesdobrClinicaId.setMdsSeq(fatMotivoDesdobramento.getSeq());
		
		FatMotivoDesdobrClinica fatMotivoDesdobrClinica = new FatMotivoDesdobrClinica();
		fatMotivoDesdobrClinica.setId(fatMotivoDesdobrClinicaId);
		
		faturamentoFacade.excluirClinicaMotivoDesdobramento(fatMotivoDesdobrClinicaId);
		
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_MOTIVO_DESDOBRAMENTO_CLINICA", clinicaSelecionada.getDescricao());
		
		listaClinicas = faturamentoFacade.pesquisarClinicasPorMotivoDesdobramento(this.fatMotivoDesdobramento.getSeq());
	}
	
	/**
	 * Edita motivo de desdobramento SSM.
	 */	              
	public void editarMotivoDesdobramentoSSM() {
		fatMotivoDesdobrSsmVO.setEditando(Boolean.TRUE);
		fatMotivoDesdobrSsm = fatMotivoDesdobrSsmVO.getFatMotivoDesdobrSsm();
		
		diasInternacaoProcedimento = fatMotivoDesdobrSsm.getDiasInternacao();
		
		if (fatMotivoDesdobrSsm.getIndSituacao().equals(DominioSituacao.A)) {
			ativoProcedimento = true;
		} else {
			ativoProcedimento = false;
		}
		
		fatItensProcedHospitalar = fatMotivoDesdobrSsm.getItemProcedimentoHospitalar();
		
		edicaoMotivoDesdobramentoSSM = true;
	}
	
	/**
	 * Altera motivo de desdobramento SSM.
	 */
	public void alterarMotivoDesdobramentoSSM() {
		
		FatMotivoDesdobrSsmId fatMotivoDesdobrSsmId = new FatMotivoDesdobrSsmId();
		fatMotivoDesdobrSsmId.setIphSeq(fatItensProcedHospitalar.getId().getSeq());
		fatMotivoDesdobrSsmId.setIphPhoSeq(fatItensProcedHospitalar.getId().getPhoSeq());		
		fatMotivoDesdobrSsmId.setMdsSeq(fatMotivoDesdobramento.getSeq());
		
		fatMotivoDesdobrSsm.setId(fatMotivoDesdobrSsmId);
		fatMotivoDesdobrSsm.setDiasInternacao(this.diasInternacaoProcedimento);
		
		if (ativoProcedimento) {
			fatMotivoDesdobrSsm.setIndSituacao(DominioSituacao.A);
		} else {
			fatMotivoDesdobrSsm.setIndSituacao(DominioSituacao.I);
		}

		fatMotivoDesdobrSsm.setAlteradoPor(super.obterLoginUsuarioLogado());
		fatMotivoDesdobrSsm.setAlteradoEm(new Date());
		
		faturamentoFacade.alterarMotivoDesdobramentoSSM(fatMotivoDesdobrSsm);
		
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_DESDOBRAMENTO_SSM", fatMotivoDesdobrSsm.getItemProcedimentoHospitalar().getDescricao());
		
		List<FatMotivoDesdobrSsm> listaFatMotivoDesdobrSsm = faturamentoFacade.pesquisarMotivosDesdobramentosSSM(this.fatMotivoDesdobramento.getSeq());
		
		this.transformarEmListaDeVO(listaFatMotivoDesdobrSsm);
		
		fatMotivoDesdobrSsmVO.setEditando(Boolean.FALSE);
		edicaoMotivoDesdobramentoSSM = false;
		
		this.limparCamposAba2();
	}
	
	/**
	 * Cancela edição de motivo de desdobramento SSM.
	 */
	public void cancelarEdicaoMotivoDesdobramentoSSM() {
		this.limparCamposAba2();
		fatMotivoDesdobrSsmVO.setEditando(Boolean.FALSE);
		edicaoMotivoDesdobramentoSSM = false;
	}
	
	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {
		edicao = false;
		return PAGE_PESQUISA_MOTIVOS_DESDOBRAMENTO;
	}
	
	/**
	 * Limpa campos da segunda aba.
	 */
	private void limparCamposAba2() {
		
		fatItensProcedHospitalar = null;
		fatMotivoDesdobrSsm = new FatMotivoDesdobrSsm();
		diasInternacaoProcedimento = null;
		ativoProcedimento = true;
	}
	
	/**
	 * Método usado no suggestionBox de Tipo AIH.
	 * @param parametro
	 * @return Quantidade.
	 */
	public Long pesquisarTipoAIHCount(String parametro) {
		return faturamentoFacade.pesquisarTipoAihCount(parametro);
	}

	/**
	 * Método usado no suggestionBox de Tipo AIH.
	 * @param parametro
	 * @return Lista de TipoAIH.
	 */
	public List<FatTipoAih> pesquisarTipoAIH(String parametro) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarTipoAih(parametro),pesquisarTipoAIHCount(parametro));
	}

	/**
	 * Método usado no suggestionBox de Clínica.
	 * @param parametro
	 * @return Quantidade.
	 */
	public Long pesquisarClinicasCount(String parametro) {
		return faturamentoFacade.pesquisarClinicasPorCodigoOuDescricaoCount(parametro);
	}
	
	/**
	 * Método usado no suggestionBox de Clínica.
	 * @return Lista de clínicas.
	 */
	public List<AghClinicas> pesquisarClinicas(String parametro) {
		return faturamentoFacade.pesquisarClinicasPorCodigoOuDescricao(parametro);
	}
	
	/**
	 * Método usado no suggestionBox de Procedimento.
	 * @param parametro
	 * @return Quantidade
	 */
	public Long pesquisarProcedimentosCount(String parametro) {
		return faturamentoFacade.pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricaoCount(parametro);
	}
	
	/**
	 * Método usado no suggestionBox de Procedimento.
	 * @param parametro
	 * @return Lista de procedimentos.
	 */
	public List<FatItensProcedHospitalar> pesquisarProcedimentos(String parametro) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarProcedimentosPorTabelaOuItemOuProcedimentoOuDescricao(parametro),pesquisarProcedimentosCount(parametro));
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterDescricaoTruncada(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}
	
	//
	//GETs e SETs
	//
	
	public FatMotivoDesdobramento getFatMotivoDesdobramento() {
		return fatMotivoDesdobramento;
	}

	public void setFatMotivoDesdobramento(
			FatMotivoDesdobramento fatMotivoDesdobramento) {
		this.fatMotivoDesdobramento = fatMotivoDesdobramento;
	}

	public AghClinicas getAghClinicas() {
		return aghClinicas;
	}

	public void setAghClinicas(AghClinicas aghClinicas) {
		this.aghClinicas = aghClinicas;
	}

	public List<AghClinicas> getListaClinicas() {
		return listaClinicas;
	}

	public void setListaClinicas(List<AghClinicas> listaClinicas) {
		this.listaClinicas = listaClinicas;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AghClinicas getClinicaSelecionada() {
		return clinicaSelecionada;
	}

	public void setClinicaSelecionada(AghClinicas clinicaSelecionada) {
		this.clinicaSelecionada = clinicaSelecionada;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}	

	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public void setFatItensProcedHospitalar(
			FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}

	public Boolean getAtivoProcedimento() {
		return ativoProcedimento;
	}

	public void setAtivoProcedimento(Boolean ativoProcedimento) {
		this.ativoProcedimento = ativoProcedimento;
	}

	public FatMotivoDesdobrSsm getFatMotivoDesdobrSsm() {
		return fatMotivoDesdobrSsm;
	}

	public void setFatMotivoDesdobrSsm(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) {
		this.fatMotivoDesdobrSsm = fatMotivoDesdobrSsm;
	}

	public List<FatMotivoDesdobrSsmVO> getListaMotivosDesdobramentosSSM() {
		return listaMotivosDesdobramentosSSM;
	}

	public void setListaMotivosDesdobramentosSSM(
			List<FatMotivoDesdobrSsmVO> listaMotivosDesdobramentosSSM) {
		this.listaMotivosDesdobramentosSSM = listaMotivosDesdobramentosSSM;
	}

	public boolean isEdicaoMotivoDesdobramentoSSM() {
		return edicaoMotivoDesdobramentoSSM;
	}

	public void setEdicaoMotivoDesdobramentoSSM(boolean edicaoMotivoDesdobramentoSSM) {
		this.edicaoMotivoDesdobramentoSSM = edicaoMotivoDesdobramentoSSM;
	}

	public FatMotivoDesdobrSsmVO getFatMotivoDesdobrSsmVO() {
		return fatMotivoDesdobrSsmVO;
	}

	public void setFatMotivoDesdobrSsmVO(FatMotivoDesdobrSsmVO fatMotivoDesdobrSsmVO) {
		this.fatMotivoDesdobrSsmVO = fatMotivoDesdobrSsmVO;
	}

	public FatTipoAih getTipoAihSuggestion() {
		return tipoAihSuggestion;
	}

	public void setTipoAihSuggestion(FatTipoAih tipoAihSuggestion) {
		this.tipoAihSuggestion = tipoAihSuggestion;
	}
					
	public Short getDiasInternacaoProcedimento() {
		return diasInternacaoProcedimento;
}

	public void setDiasInternacaoProcedimento(Short diasInternacaoProcedimento) {
		this.diasInternacaoProcedimento = diasInternacaoProcedimento;
	}
					
}
