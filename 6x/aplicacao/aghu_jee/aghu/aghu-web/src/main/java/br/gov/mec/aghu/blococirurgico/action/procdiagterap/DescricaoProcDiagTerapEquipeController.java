package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalEquipePdt;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtProfId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 *
 * Controller para a aba Equipe (Procedimento Diagnóstico Terapêutico).
 * 
 * @author dpacheco
 *
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class DescricaoProcDiagTerapEquipeController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapEquipeController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 928716147110522092L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;
	
	private ProfDescricaoCirurgicaVO equipe;
	private ProfDescricaoCirurgicaVO substituto;
	
	// Atributos para Equipe Cirurgia 
	private ProfDescricaoCirurgicaVO profissionalEquipeCrg;
	private DominioTipoAtuacao tipoAtuacaoProfEquipeCirurgia;
	private List<DominioTipoAtuacao> listaTipoAtuacaoProfEquipeCirurgia;
	private List<ProfDescricaoCirurgicaVO> listaProfEquipeCirurgia;
	
	// Atributos para Anestesia 
	private ProfDescricaoCirurgicaVO anestesista;
	private List<ProfDescricaoCirurgicaVO> listaProfAnestesista;
	
	// Atributos para Equipe de Enfermagem
	private ProfDescricaoCirurgicaVO enfermeiro;
	private List<ProfDescricaoCirurgicaVO> listaProfEnfermeiro;
	
	// Atributos para Outros Profissionais
	private String nomeOutroProfissional;
	private DominioCategoriaProfissionalEquipePdt categoriaOutroProfissional;
	private List<PdtProf> listaProfOutros;	
	
	private DominioSituacao situacaoProfAtuaCirg;
	private List<String> listaSiglaConselho;
	private List<String> listaSiglaConselhoEnf;
	private RapServidores servidorLogado;
	private String siglaConselhoRegionalEnf;
	private Integer ddtSeq;
	private Integer ddtCrgSeq;
	
	private DominioTipoAtuacao tipoAtuacaoExc;
	private Short serVinCodigoExc;
	private Integer serMatriculaExc;	
	private Short seqpExc;
	
	private List<PdtProf> listaProf;
	
	// Parametros relacionados a cirurgia selecionada previamente
	private AghUnidadesFuncionais unidadeFuncionalCrg;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
		
		ddtSeq = descricaoProcDiagTerapVO.getDdtSeq();
		ddtCrgSeq = descricaoProcDiagTerapVO.getDdtCrgSeq();
		
		listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfOutros = new ArrayList<PdtProf>();
		
		DominioTipoAtuacao[] arrayTipoAtuacaoEquipeCrg = {DominioTipoAtuacao.AUX, DominioTipoAtuacao.CIRG, DominioTipoAtuacao.SUP};
		listaTipoAtuacaoProfEquipeCirurgia = Arrays.asList(arrayTipoAtuacaoEquipeCrg);
		
		try {
			servidorLogado = registroColaboradorFacade
					.obterServidorPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		try {
			
			String strListaSiglaConselho = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			listaSiglaConselho = Arrays.asList(strListaSiglaConselho.split(","));
			siglaConselhoRegionalEnf = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SIGLA_CONSELHO_REGIONAL_ENF);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}		
		
		listaSiglaConselhoEnf = new ArrayList<String>();
		listaSiglaConselhoEnf.add(siglaConselhoRegionalEnf);

		situacaoProfAtuaCirg = DominioSituacao.A;
		
		unidadeFuncionalCrg = descricaoProcDiagTerapVO.getUnidadeFuncional();

		substituto = null;
		
		listaProf = blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeq(ddtSeq);
		carregarValoresSalvos(listaProf);

		setTipoAtuacaoProfEquipeCirurgia(DominioTipoAtuacao.AUX);
		
	}
	
	public void carregarValoresSalvos(List<PdtProf> listaProf) {
		List<ProfDescricaoCirurgicaVO> listaProfAtuaUnidCirgConselhoVO = null;
		
		for (PdtProf prof : listaProf) {
			RapServidores servidorProf = prof.getServidorPrf();
			DominioTipoAtuacao tipoAtuacao = prof.getTipoAtuacao();
			
			if (DominioTipoAtuacao.RESP.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					equipe = listaProfAtuaUnidCirgConselhoVO.get(0);
				}
			} else if (DominioTipoAtuacao.SUBS.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					substituto = listaProfAtuaUnidCirgConselhoVO.get(0);	
				}
			} else if (DominioTipoAtuacao.SUP.equals(tipoAtuacao)
					|| DominioTipoAtuacao.CIRG.equals(tipoAtuacao)
					|| DominioTipoAtuacao.AUX.equals(tipoAtuacao)) {
				
				if (DominioTipoAtuacao.SUP.equals(tipoAtuacao)) {
					listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				} else if (DominioTipoAtuacao.CIRG.equals(tipoAtuacao) || DominioTipoAtuacao.AUX.equals(tipoAtuacao)) {
					listaProfAtuaUnidCirgConselhoVO = registroColaboradorFacade.pesquisarServidorConselhoListaSiglaPorServidor(
							listaSiglaConselho, servidorProf.getId().getMatricula(), servidorProf.getId().getVinCodigo());
				}
				
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					ProfDescricaoCirurgicaVO profVO = listaProfAtuaUnidCirgConselhoVO.get(0);
					profVO.setTipoAtuacao(tipoAtuacao);
					listaProfEquipeCirurgia.add(profVO);
				}
			} else if (DominioTipoAtuacao.ANES.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					ProfDescricaoCirurgicaVO profVO = listaProfAtuaUnidCirgConselhoVO.get(0);
					profVO.setTipoAtuacao(tipoAtuacao);
					listaProfAnestesista.add(profVO);
				}
			} else if (DominioTipoAtuacao.ENF.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselhoEnfermagem(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					ProfDescricaoCirurgicaVO profVO = listaProfAtuaUnidCirgConselhoVO.get(0);
					profVO.setTipoAtuacao(tipoAtuacao);
					listaProfEnfermeiro.add(profVO);
				}
			} else if (DominioTipoAtuacao.OUTR.equals(tipoAtuacao)) {
				listaProfOutros.add(prof);
			}
		}
	}
	
	public void gravarEquipe() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.RESP;
		equipe.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, equipe);
			//exibirMensagemSucessoInclusao();
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void gravarSubstituto() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.SUBS;
		substituto.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, substituto);
			//exibirMensagemSucessoInclusao();	
			substituto = null;
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void gravarProfissionalEquipeCrg() {
		profissionalEquipeCrg.setTipoAtuacao(tipoAtuacaoProfEquipeCirurgia);
		if(verificarEquipeSedacao()){
			try {
				blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, profissionalEquipeCrg);
				//exibirMensagemSucessoInclusao();
				listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
				carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqEListaTipoAtuacao(ddtSeq, listaTipoAtuacaoProfEquipeCirurgia));
				limparCamposProfissionalEquipeCrg();
				relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private Boolean verificarEquipeSedacao(){
		List<ProfDescricaoCirurgicaVO> listaProfAtuaUnidCirgConselhoVO = null;
		listaProf = blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeq(ddtSeq);
		for (PdtProf prof : listaProf) {
			//#53595 - responsável pela equipe também pode ser o executor da sedação
			if(!DominioTipoAtuacao.RESP.equals(prof.getTipoAtuacao())) {
				RapServidores servidorProf = prof.getServidorPrf();
				if(prof.getServidorPrf() != null ) {
					listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
					if (listaProfAtuaUnidCirgConselhoVO != null && listaProfAtuaUnidCirgConselhoVO.size() > 0) {
						ProfDescricaoCirurgicaVO equipe = listaProfAtuaUnidCirgConselhoVO.get(0);
						if(equipe.getNroRegConselho() != null && equipe.getNroRegConselho().equals(profissionalEquipeCrg.getNroRegConselho())){
							profissionalEquipeCrg=null;
							this.apresentarMsgNegocio(Severity.ERROR, 
									"MENSAGEM_PROFISSIONAL_MESMA_EQUIPE_CIRURGICA");
							return false;
						}
					}
				}
			}

		} 
		return true;
	}
	
	public void gravarAnestesista() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ANES;
		anestesista.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, anestesista);
			//exibirMensagemSucessoInclusao();		
			anestesista = null;
			listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void gravarEnfermeiro() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ENF;
		enfermeiro.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, enfermeiro);
			//exibirMensagemSucessoInclusao();			
			enfermeiro = null;
			listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void gravarOutroProfissional() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.OUTR;
		ProfDescricaoCirurgicaVO outroProfissional = new ProfDescricaoCirurgicaVO();
		outroProfissional.setTipoAtuacao(tipoAtuacao);
		outroProfissional.setNome(nomeOutroProfissional);
		outroProfissional.setCategoriaPdt(categoriaOutroProfissional);
		try {
			blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, outroProfissional);
			//exibirMensagemSucessoInclusao();
			listaProfOutros = new ArrayList<PdtProf>();
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
			limparCamposOutroProfissional();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void limparCamposProfissionalEquipeCrg() {
		profissionalEquipeCrg = null;
		tipoAtuacaoProfEquipeCirurgia = null;
	}
	
	private void limparCamposOutroProfissional() {
		nomeOutroProfissional = null;
		categoriaOutroProfissional = null;
	}
	
	public void removerEquipe() {
		removerProfDescricaoPorTipoAtuacao(DominioTipoAtuacao.RESP);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void removerSubstituto() {
		removerProfDescricaoPorTipoAtuacao(DominioTipoAtuacao.SUBS);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	
	
	public void removerProfissionalOutro() {
		final PdtProf prof = blocoCirurgicoProcDiagTerapFacade.obterPdtProfPorChavePrimaria(new PdtProfId(ddtSeq, seqpExc));
		blocoCirurgicoProcDiagTerapFacade.removerProf(prof);
		//exibirMensagemSucessoExclusao();
		listaProfOutros.remove(prof);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
		
	private void removerProfDescricaoPorTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		List<PdtProf> listaProf = 
				blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao);
		if (!listaProf.isEmpty()) {
			blocoCirurgicoProcDiagTerapFacade.removerProf(listaProf.get(0));
		//	exibirMensagemSucessoExclusao();
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
		}
	}
	
	public void removerProfDescricaoPorServidorProfETipoAtuacao() {
		
		List<PdtProf> listaProf = 
				blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqServidorETipoAtuacao(ddtSeq, serMatriculaExc, serVinCodigoExc, tipoAtuacaoExc);
		if (!listaProf.isEmpty()) {
			blocoCirurgicoProcDiagTerapFacade.removerProf(listaProf.get(0));
		//	exibirMensagemSucessoExclusao();
			
			if (listaTipoAtuacaoProfEquipeCirurgia.contains(tipoAtuacaoExc)) {
				listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
				carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqEListaTipoAtuacao(ddtSeq, listaTipoAtuacaoProfEquipeCirurgia));
				
			} else if (DominioTipoAtuacao.ANES.equals(tipoAtuacaoExc)) {
				listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
				carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacaoExc));
				
			} else if (DominioTipoAtuacao.ENF.equals(tipoAtuacaoExc)) {
				listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
				carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacaoExc));
			}
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		}

		tipoAtuacaoExc = null;
		serVinCodigoExc = null;
		serMatriculaExc = null;	
	}	
	
	/*private void exibirMensagemSucessoInclusao() {
		this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_PROFISSIONAL_DESCRICAO_CIRURGICA_INSERIDO_COM_SUCESSO");	
	}
	
	private void exibirMensagemSucessoExclusao() {
		this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_PROFISSIONAL_DESCRICAO_CIRURGICA_EXCLUIDO_COM_SUCESSO");
	}	*/
	
	private List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselho(
			RapServidores servidorProf) {
		
		return blocoCirurgicoFacade
				.pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(
						servidorProf.getId().getMatricula(), 
						servidorProf.getId().getVinCodigo(),
						unidadeFuncionalCrg.getSeq(), 
						listaSiglaConselho, 
						situacaoProfAtuaCirg);
	}	
	
	private List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselhoEnfermagem(
			RapServidores servidorProf) {
		
		return blocoCirurgicoFacade
				.pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(
						servidorProf.getId().getMatricula(), 
						servidorProf.getId().getVinCodigo(),
						unidadeFuncionalCrg.getSeq(), 
						listaSiglaConselhoEnf, 
						situacaoProfAtuaCirg);
	}
	
	/**
	 * Pesquisa para suggestion "Equipe". 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	
	public List<ProfDescricaoCirurgicaVO> pesquisarEquipe(String objPesquisa) {
		DominioFuncaoProfissional[] arrayFuncaoProfissionalMedicos = {
				DominioFuncaoProfissional.MCO, 
				DominioFuncaoProfissional.MPF};
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(objPesquisa,
				unidadeFuncionalCrg.getSeq(), Arrays.asList(arrayFuncaoProfissionalMedicos), listaSiglaConselho, situacaoProfAtuaCirg), blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(objPesquisa,
						unidadeFuncionalCrg.getSeq(), Arrays.asList(arrayFuncaoProfissionalMedicos), listaSiglaConselho, situacaoProfAtuaCirg));
	}

	/**
	 * Pesquisa para suggestion "Substituto". 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarSubstituto(String objPesquisa) {
		
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(objPesquisa,
				unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.MPF, listaSiglaConselho, situacaoProfAtuaCirg), blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa,
						unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.MPF, listaSiglaConselho, situacaoProfAtuaCirg));
	}
	
	/**
	 * Pesquisa para suggestion "Profissional" (Equipe Cirurgica). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarProfissionalEquipeCrg(String objPesquisa) {
		if (DominioTipoAtuacao.SUP.equals(tipoAtuacaoProfEquipeCirurgia)) {
			DominioFuncaoProfissional[] arrayFuncaoProfissionalMedicos = {
					DominioFuncaoProfissional.MCO, 
					DominioFuncaoProfissional.MPF};

			return this.returnSGWithCount(blocoCirurgicoFacade
					.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(
							objPesquisa, unidadeFuncionalCrg.getSeq(),
							Arrays.asList(arrayFuncaoProfissionalMedicos),
							listaSiglaConselho, situacaoProfAtuaCirg), 
						blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(
									objPesquisa, unidadeFuncionalCrg.getSeq(),
									Arrays.asList(arrayFuncaoProfissionalMedicos),
									listaSiglaConselho, situacaoProfAtuaCirg));
		} else if (DominioTipoAtuacao.CIRG.equals(tipoAtuacaoProfEquipeCirurgia)) {
			return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorConselhoNroRegNaoNuloListaSigla(listaSiglaConselho, objPesquisa), 
					registroColaboradorFacade.pesquisarServidorConselhoNroRegNaoNuloListaSiglaCount(listaSiglaConselho, objPesquisa));
		} else if (DominioTipoAtuacao.AUX.equals(tipoAtuacaoProfEquipeCirurgia)) {
			return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorConselhoNroRegNuloListaSigla(listaSiglaConselho, objPesquisa), 
					registroColaboradorFacade.pesquisarServidorConselhoNroRegNuloListaSiglaCount(listaSiglaConselho, objPesquisa));
		} else {
			return this.returnSGWithCount(new ArrayList<ProfDescricaoCirurgicaVO>(), 0);
		}
	}

	/**
	 * Pesquisa para suggestion "Anestesia" (Anestesia e Sedação). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarAnestesista(String objPesquisa) {
		
		DominioFuncaoProfissional[] arrayFuncaoProfissional = {
				DominioFuncaoProfissional.ANP, 
				DominioFuncaoProfissional.ANR,
				DominioFuncaoProfissional.ANC };

		return this.returnSGWithCount(blocoCirurgicoFacade
				.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(
						objPesquisa, unidadeFuncionalCrg.getSeq(),
						Arrays.asList(arrayFuncaoProfissional),
						listaSiglaConselho, situacaoProfAtuaCirg), 
					blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(
								objPesquisa, unidadeFuncionalCrg.getSeq(),
								Arrays.asList(arrayFuncaoProfissional),
								listaSiglaConselho, situacaoProfAtuaCirg));
	}	

	/**
	 * Pesquisa para suggestion "Enfermeiro" (Equipe de Enfermagem). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarEnfermeiro(String objPesquisa) {
		
		DominioFuncaoProfissional[] arrayFuncaoProfissional = {
				DominioFuncaoProfissional.ENF, 
				DominioFuncaoProfissional.CIR,
				DominioFuncaoProfissional.INS };

		return this.returnSGWithCount(blocoCirurgicoFacade
				.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(
						objPesquisa, unidadeFuncionalCrg.getSeq(),
						Arrays.asList(arrayFuncaoProfissional),
						listaSiglaConselhoEnf, situacaoProfAtuaCirg), 
					blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(
								objPesquisa, unidadeFuncionalCrg.getSeq(),
								Arrays.asList(arrayFuncaoProfissional),
								listaSiglaConselhoEnf, situacaoProfAtuaCirg));
	}

	public void ajustaSuggestionsEquipeCirurgica(){
		profissionalEquipeCrg = null;
	}
	
	/*
	 * Getters e setters
	 */	
	public DescricaoProcDiagTerapVO getDescricaoProcDiagTerapVO() {
		return descricaoProcDiagTerapVO;
	}

	public void setDescricaoProcDiagTerapVO(
			DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
	}

	public ProfDescricaoCirurgicaVO getEquipe() {
		return equipe;
	}

	public void setEquipe(ProfDescricaoCirurgicaVO equipe) {
		this.equipe = equipe;
	}

	public ProfDescricaoCirurgicaVO getSubstituto() {
		return substituto;
	}

	public void setSubstituto(ProfDescricaoCirurgicaVO substituto) {
		this.substituto = substituto;
	}

	public ProfDescricaoCirurgicaVO getProfissionalEquipeCrg() {
		return profissionalEquipeCrg;
	}

	public void setProfissionalEquipeCrg(
			ProfDescricaoCirurgicaVO profissionalEquipeCrg) {
		this.profissionalEquipeCrg = profissionalEquipeCrg;
	}

	public DominioTipoAtuacao getTipoAtuacaoProfEquipeCirurgia() {
		return tipoAtuacaoProfEquipeCirurgia;
	}

	public void setTipoAtuacaoProfEquipeCirurgia(
			DominioTipoAtuacao tipoAtuacaoProfEquipeCirurgia) {
		this.tipoAtuacaoProfEquipeCirurgia = tipoAtuacaoProfEquipeCirurgia;
	}

	public List<DominioTipoAtuacao> getListaTipoAtuacaoProfEquipeCirurgia() {
		return listaTipoAtuacaoProfEquipeCirurgia;
	}

	public void setListaTipoAtuacaoProfEquipeCirurgia(
			List<DominioTipoAtuacao> listaTipoAtuacaoProfEquipeCirurgia) {
		this.listaTipoAtuacaoProfEquipeCirurgia = listaTipoAtuacaoProfEquipeCirurgia;
	}

	public List<ProfDescricaoCirurgicaVO> getListaProfEquipeCirurgia() {
		return listaProfEquipeCirurgia;
	}

	public void setListaProfEquipeCirurgia(
			List<ProfDescricaoCirurgicaVO> listaProfEquipeCirurgia) {
		this.listaProfEquipeCirurgia = listaProfEquipeCirurgia;
	}
	
	public ProfDescricaoCirurgicaVO getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(ProfDescricaoCirurgicaVO anestesista) {
		this.anestesista = anestesista;
	}

	public List<ProfDescricaoCirurgicaVO> getListaProfAnestesista() {
		return listaProfAnestesista;
	}

	public void setListaProfAnestesista(
			List<ProfDescricaoCirurgicaVO> listaProfAnestesista) {
		this.listaProfAnestesista = listaProfAnestesista;
	}

	public ProfDescricaoCirurgicaVO getEnfermeiro() {
		return enfermeiro;
	}

	public void setEnfermeiro(ProfDescricaoCirurgicaVO enfermeiro) {
		this.enfermeiro = enfermeiro;
	}

	public List<ProfDescricaoCirurgicaVO> getListaProfEnfermeiro() {
		return listaProfEnfermeiro;
	}

	public void setListaProfEnfermeiro(
			List<ProfDescricaoCirurgicaVO> listaProfEnfermeiro) {
		this.listaProfEnfermeiro = listaProfEnfermeiro;
	}

	public String getNomeOutroProfissional() {
		return nomeOutroProfissional;
	}

	public void setNomeOutroProfissional(String nomeOutroProfissional) {
		this.nomeOutroProfissional = nomeOutroProfissional;
	}

	public DominioCategoriaProfissionalEquipePdt getCategoriaOutroProfissional() {
		return categoriaOutroProfissional;
	}

	public void setCategoriaOutroProfissional(
			DominioCategoriaProfissionalEquipePdt categoriaOutroProfissional) {
		this.categoriaOutroProfissional = categoriaOutroProfissional;
	}

	public List<PdtProf> getListaProfOutros() {
		return listaProfOutros;
	}

	public void setListaProfOutros(List<PdtProf> listaProfOutros) {
		this.listaProfOutros = listaProfOutros;
	}

	public DominioSituacao getSituacaoProfAtuaCirg() {
		return situacaoProfAtuaCirg;
	}

	public void setSituacaoProfAtuaCirg(DominioSituacao situacaoProfAtuaCirg) {
		this.situacaoProfAtuaCirg = situacaoProfAtuaCirg;
	}

	public List<String> getListaSiglaConselho() {
		return listaSiglaConselho;
	}

	public void setListaSiglaConselho(List<String> listaSiglaConselho) {
		this.listaSiglaConselho = listaSiglaConselho;
	}

	public List<String> getListaSiglaConselhoEnf() {
		return listaSiglaConselhoEnf;
	}

	public void setListaSiglaConselhoEnf(List<String> listaSiglaConselhoEnf) {
		this.listaSiglaConselhoEnf = listaSiglaConselhoEnf;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Integer getDdtCrgSeq() {
		return ddtCrgSeq;
	}

	public void setDdtCrgSeq(Integer ddtCrgSeq) {
		this.ddtCrgSeq = ddtCrgSeq;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalCrg() {
		return unidadeFuncionalCrg;
	}

	public void setUnidadeFuncionalCrg(AghUnidadesFuncionais unidadeFuncionalCrg) {
		this.unidadeFuncionalCrg = unidadeFuncionalCrg;
	}

	public DominioTipoAtuacao getTipoAtuacaoExc() {
		return tipoAtuacaoExc;
	}

	public void setTipoAtuacaoExc(DominioTipoAtuacao tipoAtuacaoExc) {
		this.tipoAtuacaoExc = tipoAtuacaoExc;
	}

	public Short getSerVinCodigoExc() {
		return serVinCodigoExc;
	}

	public void setSerVinCodigoExc(Short serVinCodigoExc) {
		this.serVinCodigoExc = serVinCodigoExc;
	}

	public Integer getSerMatriculaExc() {
		return serMatriculaExc;
	}

	public void setSerMatriculaExc(Integer serMatriculaExc) {
		this.serMatriculaExc = serMatriculaExc;
	}

	public Short getSeqpExc() {
		return seqpExc;
	}

	public void setSeqpExc(Short seqpExc) {
		this.seqpExc = seqpExc;
	}

	public List<PdtProf> getListaProf() {
		return listaProf;
	}

	public void setListaProf(List<PdtProf> listaProf) {
		this.listaProf = listaProf;
	}

	

	

}
