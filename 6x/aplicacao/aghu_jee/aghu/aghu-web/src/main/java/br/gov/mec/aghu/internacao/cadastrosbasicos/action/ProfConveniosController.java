package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenioId;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de convenios
 * para profissionais.
 * 
 */

public class ProfConveniosController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 998170389176911222L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	/** Entidade mãe. */
	private AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();

	/** Entidade filha. Usada para associar */
	private AghProfissionaisEspConvenio aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();

	/**
	 * Os atributos abaixo são passsados um a um pois como se tratá de um VO, é
	 * necessário uma nova pesquisa para obter o objeto a ser gerenciado.
	 */
	private Integer serMatricula;
	private Integer serVinCodigo;
	private Integer espSeq;
	private String nome;
	private String cpfTemp;
	private Long cpf;

	/**
	 * Variável que indica o estado de edição de um AghProfissionaisEspConvenio.
	 */
	private Boolean isUpdate;

	/**
	 * LOV Convênio
	 */
	private Short codigoConveniosSaude;
	private String codDescConvSaude;
	private FatConvenioSaude convenioSaude;
	private List<FatConvenioSaude> conveniosSaudes = new ArrayList<FatConvenioSaude>();
	
	private Boolean confirmaVoltar;
	private AghProfissionaisEspConvenio aghProfConveniosExclusao;
	private List<AghProfissionaisEspConvenio> listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>();
	
	private final String PAGE_LIST_PROF_CONVENIOS =  "profConveniosList";
	
	private ProfConveniosListVO profConveniosListVo;
	
	
	/*@PostConstruct
	public void init() {
		this.setAghProfEspecialidades(new AghProfEspecialidades());		
	}*/
	
	/**
	 * Metodo chamado ao iniciar a tela.
	 * 
	 * @return
	 */
	
	public void inicio() {		
		
		
		if(this.profConveniosListVo != null){
			this.setSerMatricula(Integer.valueOf(this.profConveniosListVo.getSerMatricula()));
			this.setSerVinCodigo(Integer.valueOf(this.profConveniosListVo.getVinCodigo()));
			this.setEspSeq(Integer.valueOf(this.profConveniosListVo.getSeqEspecialidade()));
			this.setNome(this.profConveniosListVo.getNome());
			this.setCpfTemp(this.profConveniosListVo.getCpf());
			
			this.cpf = !"".equals(this.cpfTemp) ? Long.valueOf(cpfTemp) : null;
			
			this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade
					.obterAghProfEspecialidades(this.serMatricula, this.serVinCodigo, this.espSeq);			
			
		}

		this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(
				this.aghProfEspecialidades.getProfissionaisEspConvenio());
		
        if (this.isUpdate != null && !this.isUpdate){
			this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
			this.aghProfissionaisEspConvenio.setIndAtuaCirurgiaoResponsavel(false);
			this.aghProfissionaisEspConvenio.setIndRecebeHcpa(false);
			this.aghProfissionaisEspConvenio.setIndInterna(false);
			this.aghProfissionaisEspConvenio.setSituacao(true);		
			this.codigoConveniosSaude = null;
			this.codDescConvSaude = null;
			this.convenioSaude = null;
			this.conveniosSaudes = new ArrayList<FatConvenioSaude>();
        }

		this.isUpdate = false;
		this.confirmaVoltar = false;		
		
		
	}

	/**
	 * Método que associa AghProfissionaisEspConvenio à lista de
	 * AghProfEspecialidades.
	 */
	public void associarConvenio() {
		try {
			if (this.convenioSaude == null
					|| this.convenioSaude.getCodigo() == null) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_CONVENIO_PROFISSIONAL_NAO_INFORMADO");
	
				return;
			}
	
			AghProfissionaisEspConvenioId id = new AghProfissionaisEspConvenioId();
	
			id.setCnvCodigo(this.convenioSaude.getCodigo());
			id.setPreSerMatricula(this.aghProfEspecialidades.getId().getSerMatricula());
			id.setPreSerVinCodigo(this.aghProfEspecialidades.getId().getSerVinCodigo());
			id.setPreEspSeq(this.aghProfEspecialidades.getId().getEspSeq());
	
			this.aghProfissionaisEspConvenio.setId(id);
	
			this.aghProfissionaisEspConvenio.setFatConvenioSaude(this.convenioSaude);
	
			boolean convenioJaEscolhido = false;
			for (AghProfissionaisEspConvenio _profissionalConvenio : this.aghProfEspecialidades.getProfissionaisEspConvenio()) {
				if (_profissionalConvenio.getId().getCnvCodigo().equals(this.aghProfissionaisEspConvenio.getId().getCnvCodigo())) {
					convenioJaEscolhido = true;
					break;
				}
			}
	
			if (convenioJaEscolhido) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_CONVENIO_PROFISSIONAL_JA_EXISTENTE");
				this.codigoConveniosSaude = null;
				return;
			}
	
			// Insere no banco
			this.cadastrosBasicosInternacaoFacade.inserirAghProfEspConvenios(this.aghProfissionaisEspConvenio);
	
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_PERSISTIR_CONVENIO");
	
			this.isUpdate = false;

			// Reinicia a paginação.
			// FR reiniciarPaginator(ProfConveniosListPaginatorController.class);
	
			this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.obterAghProfEspecialidades(serMatricula, serVinCodigo, espSeq);
			
			this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(this.aghProfEspecialidades.getProfissionaisEspConvenio());
			
			this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
			this.aghProfissionaisEspConvenio.setIndAtuaCirurgiaoResponsavel(false);
			this.aghProfissionaisEspConvenio.setIndRecebeHcpa(true);
			this.aghProfissionaisEspConvenio.setIndInterna(false);
			this.aghProfissionaisEspConvenio.setSituacao(true);
	
			this.codigoConveniosSaude = null;
			this.codDescConvSaude = null;
			this.convenioSaude = null;
			this.conveniosSaudes = new ArrayList<FatConvenioSaude>();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}

	public void remover() {
		if (this.aghProfConveniosExclusao != null) {
			if (this.cadastrosBasicosInternacaoFacade.verificarExclusao(this.serMatricula,
					this.serVinCodigo, this.espSeq,
					this.aghProfConveniosExclusao.getId().getCnvCodigo())) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_EXCLUSAO_CONVENIO_DEPENDENCIA_ASSINAT_LAUDO");
			} else {
				try {
					this.cadastrosBasicosInternacaoFacade.removerAghProfEspConvenios(aghProfConveniosExclusao);

					this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.obterAghProfEspecialidades(serMatricula, serVinCodigo, espSeq);
					
					this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(this.aghProfEspecialidades.getProfissionaisEspConvenio());
					
					this.reiniciarVariaveis();

					// Reinicia a paginação.
					// FR reiniciarPaginator(ProfConveniosListPaginatorController.class);
				} catch (ApplicationBusinessException e) {			
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	/**
	 * Limpar os campos de entrada da tela.
	 */
	public void limpar() {
		this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
		this.aghProfissionaisEspConvenio.setIndAtuaCirurgiaoResponsavel(false);
		this.aghProfissionaisEspConvenio.setIndRecebeHcpa(true);
		this.aghProfissionaisEspConvenio.setIndInterna(false);
		this.aghProfissionaisEspConvenio.setSituacao(true);

		this.convenioSaude = null;

		this.isUpdate = false;

		this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.obterAghProfEspecialidades(serMatricula, serVinCodigo, espSeq);
		
		this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(
				this.aghProfEspecialidades.getProfissionaisEspConvenio());
	}
	
	public void limparLista() {
		if (this.conveniosSaudes != null) {
			this.conveniosSaudes.clear();
		}
	}

	/**
	 * Método invocado ao clicar no icone para edicao de um
	 * AghProfissionaisEspConvenio.
	 * 
	 * @param aghProfConvenios
	 */
	public void iniciarEdicao(AghProfissionaisEspConvenio aghProfConvenios) {
		this.isUpdate = true;

		this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
		this.aghProfissionaisEspConvenio.setId(aghProfConvenios.getId());
		this.aghProfissionaisEspConvenio.setIndAtuaCirurgiaoResponsavel(aghProfConvenios.getIndAtuaCirurgiaoResponsavel());
		this.aghProfissionaisEspConvenio.setIndRecebeHcpa(aghProfConvenios.getIndRecebeHcpa());
		this.aghProfissionaisEspConvenio.setIndInterna(aghProfConvenios.getIndInterna());
		this.aghProfissionaisEspConvenio.setSituacao(aghProfConvenios.isSituacao());
		this.aghProfissionaisEspConvenio.setMatrProfConv(aghProfConvenios.getMatrProfConv());
		this.aghProfissionaisEspConvenio.setProfEspecialidade(aghProfConvenios.getProfEspecialidade());
		this.aghProfissionaisEspConvenio.setFatConvenioSaude(aghProfConvenios.getFatConvenioSaude());

		this.convenioSaude = aghProfConvenios.getFatConvenioSaude();

		this.aghuFacade.desatacharAghProfissionaisEspConvenio(this.aghProfissionaisEspConvenio);
	}

	public void cancelarEdicao() {
		this.reiniciarVariaveis();
	}
	
	/**
	 * Método invocado para salvar um AghProfissionaisEspConvenio que estava
	 * sendo editado.
	 */
	public void confirmarEdicao() {
		try {
			this.cadastrosBasicosInternacaoFacade.atualizarAghProfEspConvenios(aghProfissionaisEspConvenio);

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ATUALIZAR_CONVENIO");

			// Limpa variáveis.
			this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
			this.aghProfissionaisEspConvenio
					.setIndAtuaCirurgiaoResponsavel(false);
			this.aghProfissionaisEspConvenio.setIndRecebeHcpa(true);
			this.aghProfissionaisEspConvenio.setIndInterna(false);
			this.aghProfissionaisEspConvenio.setSituacao(true);

			this.convenioSaude = null;
			this.conveniosSaudes = new ArrayList<FatConvenioSaude>();
			this.isUpdate = false;
			
			this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade
					.obterAghProfEspecialidades(serMatricula, serVinCodigo, espSeq);
			
			this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(
					this.aghProfEspecialidades.getProfissionaisEspConvenio());
			
		// Reinicia a paginação.
		// FR reiniciarPaginator(ProfConveniosListPaginatorController.class);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void reiniciarVariaveis() {
		// Reinicia as variáveis.
		this.aghProfissionaisEspConvenio = new AghProfissionaisEspConvenio();
		this.aghProfissionaisEspConvenio.setIndAtuaCirurgiaoResponsavel(false);
		this.aghProfissionaisEspConvenio.setIndRecebeHcpa(true);
		this.aghProfissionaisEspConvenio.setIndInterna(false);
		this.aghProfissionaisEspConvenio.setSituacao(true);

		this.convenioSaude = null;

		this.isUpdate = false;
		
		this.aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade
				.obterAghProfEspecialidades(serMatricula, serVinCodigo, espSeq);
		
		this.listaProfissionaisEspConvenios = new ArrayList<AghProfissionaisEspConvenio>(
				this.aghProfEspecialidades.getProfissionaisEspConvenio());
	}
	
	/**
	 * Pesquisa FatConvenioSaude pela pk.
	 */
	public void buscarConveniosSaude() {
		if (codigoConveniosSaude != null) {
			this.convenioSaude = faturamentoApoioFacade
					.obterConvenioSaude(codigoConveniosSaude);
		} else {
			this.convenioSaude = new FatConvenioSaude();
		}
		this.limparLista();
	}

	/**
	 * Retorna uma lista de Convênios.
	 * @param param
	 * @return List<FatConvenioSaude>
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaude(String param){
		this.conveniosSaudes = this.faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricao(param);
		return this.conveniosSaudes;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Convenios para Profissionais
	 */
	public String cancelar() {
		this.confirmaVoltar = false;
		
		if (Boolean.TRUE.equals(isUpdate)) {
			this.confirmaVoltar = true;
			return null;
		}
		
		return this.voltar();
	}

	public String voltar() {
		//info("Cancelado");

		this.serVinCodigo = null;
		this.serMatricula = null;
		this.espSeq = null;
		this.nome = null;
		this.cpf = null;

		this.convenioSaude = null;
		this.conveniosSaudes = null;
		this.confirmaVoltar = false;
		
		return PAGE_LIST_PROF_CONVENIOS;
	}

	// ### GETs e SETs ###
	public void setAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio) {
		this.aghProfissionaisEspConvenio = aghProfissionaisEspConvenio;
	}

	public AghProfissionaisEspConvenio getAghProfissionaisEspConvenio() {
		return aghProfissionaisEspConvenio;
	}

	public AghProfEspecialidades getAghProfEspecialidades() {
		return aghProfEspecialidades;
	}

	public void setAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades) {
		this.aghProfEspecialidades = aghProfEspecialidades;
	}

	public List<FatConvenioSaude> getConveniosSaudes() {
		return conveniosSaudes;
	}

	public void setConveniosSaudes(List<FatConvenioSaude> conveniosSaudes) {
		this.conveniosSaudes = conveniosSaudes;
	}

	public FatConvenioSaude getConvenioSaude() {
		return convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude convenioSaude) {
		this.convenioSaude = convenioSaude;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Integer espSeq) {
		this.espSeq = espSeq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfTemp() {
		return cpfTemp;
	}

	public void setCpfTemp(String cpfTemp) {
		this.cpfTemp = cpfTemp;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Short getCodigoConveniosSaude() {
		return codigoConveniosSaude;
	}

	public void setCodigoConveniosSaude(Short codigoConveniosSaude) {
		this.codigoConveniosSaude = codigoConveniosSaude;
	}

	public String getCodDescConvSaude() {
		return codDescConvSaude;
	}

	public void setCodDescConvSaude(String codDescConvSaude) {
		this.codDescConvSaude = codDescConvSaude;
	}

	public Boolean getConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(Boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public AghProfissionaisEspConvenio getAghProfConveniosExclusao() {
		return aghProfConveniosExclusao;
	}

	public void setAghProfConveniosExclusao(AghProfissionaisEspConvenio aghProfConveniosExclusao) {
		this.aghProfConveniosExclusao = aghProfConveniosExclusao;
	}

	public List<AghProfissionaisEspConvenio> getListaProfissionaisEspConvenios() {
		return listaProfissionaisEspConvenios;
	}

	public void setListaProfissionaisEspConvenios(List<AghProfissionaisEspConvenio> listaProfissionaisEspConvenios) {
		this.listaProfissionaisEspConvenios = listaProfissionaisEspConvenios;
	}

	public ProfConveniosListVO getProfConveniosListVo() {
		return profConveniosListVo;
	}

	public void setProfConveniosListVo(ProfConveniosListVO profConveniosListVo) {
		this.profConveniosListVo = profConveniosListVo;
	}

}