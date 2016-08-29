package br.gov.mec.aghu.blococirurgico.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalEquipeCrg;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcProfDescricoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller para a aba Equipe (primeira aba para Descrição Cirúrgica).
 * 
 * @author dpacheco
 *
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class DescricaoCirurgicaEquipeController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 320117063174843392L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;		
	
	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	private ProfDescricaoCirurgicaVO equipe;
	private ProfDescricaoCirurgicaVO substituto;
	
	// Atributos para Equipe Cirurgia 
	private ProfDescricaoCirurgicaVO profissionalEquipeCrg;
	private DominioTipoAtuacao tipoAtuacaoProfEquipeCirurgia;
	private List<DominioTipoAtuacao> listaTipoAtuacaoProfEquipeCirurgia;
	private List<ProfDescricaoCirurgicaVO> listaProfEquipeCirurgia;
	
	// Atributos para Anestesia e Sedação 
	private ProfDescricaoCirurgicaVO executorSedacao;
	private MbcProfCirurgias profEscalaAnestesia;
	private ProfDescricaoCirurgicaVO anestesista;
	private List<ProfDescricaoCirurgicaVO> listaProfAnestesista;
	
	// Atributos para Equipe de Enfermagem
	private MbcProfCirurgias profEscalaEnfermagem;
	private ProfDescricaoCirurgicaVO enfermeiro;
	private List<ProfDescricaoCirurgicaVO> listaProfEnfermeiro;
	
	// Atributos para Outros Profissionais
	private String nomeOutroProfissional;
	private DominioCategoriaProfissionalEquipeCrg categoriaOutroProfissional;
	private List<MbcProfDescricoes> listaProfOutros;
	
	private DominioSituacao situacaoProfAtuaCirg;
	private List<String> listaSiglaConselho;
	private List<String> listaSiglaConselhoEnf;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private Integer seqpExc;
	private RapServidores servidorLogado;
	private String siglaConselhoRegionalEnf;
	
	// Parametros relacionados a cirurgia selecionada previamente
	private AghUnidadesFuncionais unidadeFuncionalCrg;
	
	private DominioTipoAtuacao tipoAtuacaoExc;
	private Short serVinCodigoExc;
	private Integer serMatriculaExc;
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		
		dcgCrgSeq = descricaoCirurgicaVO.getDcgCrgSeq();
		dcgSeqp = descricaoCirurgicaVO.getDcgSeqp();
		
		listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
		listaProfOutros = new ArrayList<MbcProfDescricoes>();
		
		DominioTipoAtuacao[] arrayTipoAtuacao = {DominioTipoAtuacao.AUX, DominioTipoAtuacao.CIRG, DominioTipoAtuacao.SUP};
		listaTipoAtuacaoProfEquipeCirurgia = Arrays.asList(arrayTipoAtuacao);
		
		try {
			servidorLogado = registroColaboradorFacade
					.obterServidorPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		
		try {
			
			String strListaSiglaConselho = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			listaSiglaConselho = Arrays.asList(strListaSiglaConselho.split(","));
			siglaConselhoRegionalEnf = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SIGLA_CONSELHO_REGIONAL_ENF);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}

		listaSiglaConselhoEnf = new ArrayList<String>();
		listaSiglaConselhoEnf.add(siglaConselhoRegionalEnf);

		situacaoProfAtuaCirg = DominioSituacao.A;
		
		unidadeFuncionalCrg = descricaoCirurgicaVO.getUnidadeFuncional();
		
		List<MbcProfDescricoes> listaProfDescricoes = blocoCirurgicoFacade.buscarProfDescricoes(
				descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
		
		carregarValoresSalvos(listaProfDescricoes);
		
		setTipoAtuacaoProfEquipeCirurgia(DominioTipoAtuacao.AUX);
	}
	
	public void carregarValoresSalvos(List<MbcProfDescricoes> listaProfDescricoes) {
		List<ProfDescricaoCirurgicaVO> listaProfAtuaUnidCirgConselhoVO = null;
		
		for (MbcProfDescricoes profDescricao : listaProfDescricoes) {
			RapServidores servidorProf = profDescricao.getServidorProf();
			DominioTipoAtuacao tipoAtuacao = profDescricao.getTipoAtuacao();
			
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
			} else if (DominioTipoAtuacao.ESE.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					executorSedacao = listaProfAtuaUnidCirgConselhoVO.get(0);	
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
				listaProfOutros.add(profDescricao);
			}
		}
	}
	
	public void gravarEquipe() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.RESP;
		equipe.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, equipe);			
		//	exibirMensagemSucessoInclusao();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}

	public void gravarSubstituto() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.SUBS;
		substituto.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, substituto);			
		//	exibirMensagemSucessoInclusao();	
			substituto = null;
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void ajustaSuggestionsEquipeCirurgica(){
		profissionalEquipeCrg = null;
	}
	
	public void gravarProfissionalEquipeCrg() {
		profissionalEquipeCrg.setTipoAtuacao(tipoAtuacaoProfEquipeCirurgia);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, profissionalEquipeCrg);			
		//	exibirMensagemSucessoInclusao();
			listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(dcgCrgSeq, dcgSeqp, listaTipoAtuacaoProfEquipeCirurgia));
			limparCamposProfissionalEquipeCrg();
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarExecutorSedacao() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ESE;
		executorSedacao.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, executorSedacao);			
		//	exibirMensagemSucessoInclusao();				
			executorSedacao = null;
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarAnestesista() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ANES;
		anestesista.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, anestesista);			
		//	exibirMensagemSucessoInclusao();		
			anestesista = null;
			listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarAnestesistaEscala() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ANES; 
		ProfDescricaoCirurgicaVO anestEscala = new ProfDescricaoCirurgicaVO();
		anestEscala.setTipoAtuacao(tipoAtuacao);
		anestEscala.setSerMatricula(profEscalaAnestesia.getServidorPuc().getId().getMatricula());
		anestEscala.setSerVinCodigo(profEscalaAnestesia.getServidorPuc().getId().getVinCodigo());
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, anestEscala);			
		//	exibirMensagemSucessoInclusao();
			profEscalaAnestesia = null;
			listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarEnfermeiro() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ENF;
		enfermeiro.setTipoAtuacao(tipoAtuacao);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, enfermeiro);			
		//	exibirMensagemSucessoInclusao();			
			enfermeiro = null;
			listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarEnfermeiroEscala() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ENF;
		ProfDescricaoCirurgicaVO enfEscala = new ProfDescricaoCirurgicaVO();
		enfEscala.setTipoAtuacao(tipoAtuacao);
		enfEscala.setSerMatricula(profEscalaEnfermagem.getServidorPuc().getId().getMatricula());
		enfEscala.setSerVinCodigo(profEscalaEnfermagem.getServidorPuc().getId().getVinCodigo());
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, enfEscala);			
		//	exibirMensagemSucessoInclusao();
			profEscalaEnfermagem = null;
			listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}	
	
	public void gravarOutroProfissional() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.OUTR;
		ProfDescricaoCirurgicaVO outroProfissional = new ProfDescricaoCirurgicaVO();
		outroProfissional.setTipoAtuacao(tipoAtuacao);
		outroProfissional.setNome(nomeOutroProfissional);
		outroProfissional.setCategoria(categoriaOutroProfissional);
		try {
			blocoCirurgicoFacade.inserirProfDescricoes(dcgCrgSeq, dcgSeqp, outroProfissional);			
		//	exibirMensagemSucessoInclusao();
			listaProfOutros = new ArrayList<MbcProfDescricoes>();
			carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesOutrosPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
			limparCamposOutroProfissional();
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
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
		removerProfDescricaoPorTipoAtuacao(dcgCrgSeq, dcgSeqp, DominioTipoAtuacao.RESP);
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	public void removerSubstituto() {
		removerProfDescricaoPorTipoAtuacao(dcgCrgSeq, dcgSeqp, DominioTipoAtuacao.SUBS);
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	public void removerExecutorSedacao() {
		removerProfDescricaoPorTipoAtuacao(dcgCrgSeq, dcgSeqp, DominioTipoAtuacao.ESE);
	}	
	
	public void removerProfissionalOutro() {
		try {
			MbcProfDescricoes profDescricoes = blocoCirurgicoFacade.obterMbcProfDescricoesPorChavePrimaria(new MbcProfDescricoesId(dcgCrgSeq, dcgSeqp, seqpExc));
			blocoCirurgicoFacade.excluirProfDescricoes(profDescricoes);			
		//	exibirMensagemSucessoExclusao(); 
			listaProfOutros.remove(profDescricoes);
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
		
	private void removerProfDescricaoPorTipoAtuacao(Integer dcgCrgSeq, Short dcgSeqp, DominioTipoAtuacao tipoAtuacao) {
		List<MbcProfDescricoes> listaProfDescricoes = 
				blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao);
		if (!listaProfDescricoes.isEmpty()) {
			try {
				blocoCirurgicoFacade.excluirProfDescricoes(listaProfDescricoes.get(0));				
		//		exibirMensagemSucessoExclusao();
				carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacao));
				relatorioDescricaoCirurgiaController.inicio();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}
	}
	
	public void removerProfDescricaoPorServidorProfETipoAtuacao() {
		
		List<MbcProfDescricoes> listaProfDescricoes = 
				blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpServidorProfETipoAtuacao(dcgCrgSeq, dcgSeqp,
						serMatriculaExc, serVinCodigoExc, tipoAtuacaoExc);
		
		if (!listaProfDescricoes.isEmpty()) {
			try {
				blocoCirurgicoFacade.excluirProfDescricoes(listaProfDescricoes.get(0));				
	//			exibirMensagemSucessoExclusao();
				
				if (listaTipoAtuacaoProfEquipeCirurgia.contains(tipoAtuacaoExc)) {
					listaProfEquipeCirurgia = new ArrayList<ProfDescricaoCirurgicaVO>();
					carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpEListaTipoAtuacao(dcgCrgSeq, dcgSeqp, listaTipoAtuacaoProfEquipeCirurgia));

				} else if (DominioTipoAtuacao.ANES.equals(tipoAtuacaoExc)) {
					listaProfAnestesista = new ArrayList<ProfDescricaoCirurgicaVO>();
					carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacaoExc));
					
				} else if (DominioTipoAtuacao.ENF.equals(tipoAtuacaoExc)) {
					listaProfEnfermeiro = new ArrayList<ProfDescricaoCirurgicaVO>();
					carregarValoresSalvos(blocoCirurgicoFacade.pesquisarProfDescricoesPorDcgCrgSeqDcgSeqpETipoAtuacao(dcgCrgSeq, dcgSeqp, tipoAtuacaoExc));
				}
				relatorioDescricaoCirurgiaController.inicio();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
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
	}*/
	
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
		
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(objPesquisa,
				unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.MPF, listaSiglaConselho, situacaoProfAtuaCirg), blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa,
						unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.MPF, listaSiglaConselho, situacaoProfAtuaCirg));
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
	 * Pesquisa para suggestion "Executor Sedação" (Anestesia e Sedação). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarExecutorSedacao(String objPesquisa) {

		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(objPesquisa,
				unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.ESE, listaSiglaConselho, situacaoProfAtuaCirg), blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa,
						unidadeFuncionalCrg.getSeq(), DominioFuncaoProfissional.ESE, listaSiglaConselho, situacaoProfAtuaCirg));
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
	
	// Pesquisa para combo Escala Anestesia
	public List<MbcProfCirurgias> getListaProfissionalEscalaAnestesia() {
		return blocoCirurgicoFacade.pesquisarProfCirurgiasAnestesistaPorCrgSeq(dcgCrgSeq);
	}
	
	// Pesquisa para combo Escala Enfermagem
	public List<MbcProfCirurgias> getListaProfissionalEscalaEnfermagem() {
		return blocoCirurgicoFacade.pesquisarProfCirurgiasEnfermeiroPorCrgSeq(dcgCrgSeq);
	}
	
	/*
	 * Getters e setters
	 */
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

	public List<ProfDescricaoCirurgicaVO> getListaProfEquipeCirurgia() {
		return listaProfEquipeCirurgia;
	}

	public void setListaProfEquipeCirurgia(
			List<ProfDescricaoCirurgicaVO> listaProfEquipeCirurgia) {
		this.listaProfEquipeCirurgia = listaProfEquipeCirurgia;
	}

	public DominioTipoAtuacao getTipoAtuacaoProfEquipeCirurgia() {
		return tipoAtuacaoProfEquipeCirurgia;
	}

	public void setTipoAtuacaoProfEquipeCirurgia(
			DominioTipoAtuacao tipoAtuacaoProfEquipeCirurgia) {
		this.tipoAtuacaoProfEquipeCirurgia = tipoAtuacaoProfEquipeCirurgia;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalCrg() {
		return unidadeFuncionalCrg;
	}

	public void setUnidadeFuncionalCrg(AghUnidadesFuncionais unidadeFuncionalCrg) {
		this.unidadeFuncionalCrg = unidadeFuncionalCrg;
	}

	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public MbcProfCirurgias getProfEscalaAnestesia() {
		return profEscalaAnestesia;
	}

	public void setProfEscalaAnestesia(MbcProfCirurgias profEscalaAnestesia) {
		this.profEscalaAnestesia = profEscalaAnestesia;
	}

	public List<ProfDescricaoCirurgicaVO> getListaProfAnestesista() {
		return listaProfAnestesista;
	}

	public void setListaProfAnestesista(
			List<ProfDescricaoCirurgicaVO> listaProfAnestesista) {
		this.listaProfAnestesista = listaProfAnestesista;
	}

	public List<String> getListaSiglaConselho() {
		return listaSiglaConselho;
	}

	public void setListaSiglaConselho(List<String> listaSiglaConselho) {
		this.listaSiglaConselho = listaSiglaConselho;
	}

	public ProfDescricaoCirurgicaVO getExecutorSedacao() {
		return executorSedacao;
	}

	public void setExecutorSedacao(ProfDescricaoCirurgicaVO executorSedacao) {
		this.executorSedacao = executorSedacao;
	}

	public ProfDescricaoCirurgicaVO getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(ProfDescricaoCirurgicaVO anestesista) {
		this.anestesista = anestesista;
	}

	public MbcProfCirurgias getProfEscalaEnfermagem() {
		return profEscalaEnfermagem;
	}

	public void setProfEscalaEnfermagem(MbcProfCirurgias profEscalaEnfermagem) {
		this.profEscalaEnfermagem = profEscalaEnfermagem;
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

	public List<String> getListaSiglaConselhoEnf() {
		return listaSiglaConselhoEnf;
	}

	public void setListaSiglaConselhoEnf(List<String> listaSiglaConselhoEnf) {
		this.listaSiglaConselhoEnf = listaSiglaConselhoEnf;
	}

	public String getNomeOutroProfissional() {
		return nomeOutroProfissional;
	}

	public void setNomeOutroProfissional(String nomeOutroProfissional) {
		this.nomeOutroProfissional = nomeOutroProfissional;
	}

	public DominioCategoriaProfissionalEquipeCrg getCategoriaOutroProfissional() {
		return categoriaOutroProfissional;
	}

	public void setCategoriaOutroProfissional(
			DominioCategoriaProfissionalEquipeCrg categoriaOutroProfissional) {
		this.categoriaOutroProfissional = categoriaOutroProfissional;
	}

	public List<MbcProfDescricoes> getListaProfOutros() {
		return listaProfOutros;
	}

	public void setListaProfOutros(List<MbcProfDescricoes> listaProfOutros) {
		this.listaProfOutros = listaProfOutros;
	}

	public List<DominioTipoAtuacao> getListaTipoAtuacaoProfEquipeCirurgia() {
		return listaTipoAtuacaoProfEquipeCirurgia;
	}

	public void setListaTipoAtuacaoProfEquipeCirurgia(
			List<DominioTipoAtuacao> listaTipoAtuacaoProfEquipeCirurgia) {
		this.listaTipoAtuacaoProfEquipeCirurgia = listaTipoAtuacaoProfEquipeCirurgia;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
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

	public Integer getSeqpExc() {
		return seqpExc;
	}

	public void setSeqpExc(Integer seqpExc) {
		this.seqpExc = seqpExc;
	}

	public DominioTipoAtuacao getTipoAtuacaoExc() {
		return tipoAtuacaoExc;
	}

	public void setTipoAtuacaoExc(DominioTipoAtuacao tipoAtuacaoExc) {
		this.tipoAtuacaoExc = tipoAtuacaoExc;
	}

}
