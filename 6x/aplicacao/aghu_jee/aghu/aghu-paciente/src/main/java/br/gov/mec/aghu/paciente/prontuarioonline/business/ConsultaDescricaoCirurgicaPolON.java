package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcFiguraDescricoes;
import br.gov.mec.aghu.model.MbcImagemDescricoes;
import br.gov.mec.aghu.model.MbcNotaAdicionais;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaNotasPolON.ConsultaNotasPolONExceptionCode;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.DescricaoCirurgiaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ConsultaDescricaoCirurgicaPolON extends BaseBusiness {

	
	@EJB
	private ConsultaDescricaoCirurgicaPolRN consultaDescricaoCirurgicaPolRN;
	
	private static final Log LOG = LogFactory.getLog(ConsultaDescricaoCirurgicaPolON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2457711676523203022L;
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Collection<DescricaoCirurgiaVO> pesquisarDescricaoCirurgicaPol(Integer crgSeq, Short seqp, Boolean previa, String contextPath) throws BaseException {
		
		
		// Efetua consultas
		//MbcDescricaoCirurgica descricaoCirurgica = new MbcDescricaoCirurgica();
		List<DescricaoCirurgiaVO> descricoesCirurgicas = new ArrayList<DescricaoCirurgiaVO>();
		List<MbcDescricaoCirurgica> listaDescricoesCirurgicas = null;
		
		listaDescricoesCirurgicas = getBlocoCirurgicoFacade().listarMbcDescricaoCirurgica(crgSeq, seqp); //C1
		
		for(MbcDescricaoCirurgica descricaoCirurgica : listaDescricoesCirurgicas){
			
			DescricaoCirurgiaVO descricaoCirurgicaVO = new DescricaoCirurgiaVO();
			
			//if(seqp == null){
				seqp = descricaoCirurgica.getId().getSeqp();
			//}
			AghEspecialidades especialidade = null;
			if(descricaoCirurgica.getEspecialidade() != null){
				especialidade = new AghEspecialidades();
				especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(descricaoCirurgica.getEspecialidade().getSeq()); //C2
			}
			
			List<MbcProfDescricoes> profDescricoes = new ArrayList<MbcProfDescricoes>();
			profDescricoes = getBlocoCirurgicoFacade().buscarProfDescricoes(crgSeq, seqp); //-C3
			
			MbcDescricaoItens descricaoItens = new MbcDescricaoItens();
			descricaoItens = getBlocoCirurgicoFacade().buscarDescricaoItens(crgSeq, seqp); //-C4
			
			descricaoCirurgicaVO.setProcedimentosList(getBlocoCirurgicoFacade().buscarProcDescricoes(crgSeq, seqp.intValue())); //-C5
			
			MbcAnestesiaDescricoes anestesiaDescricoes = new MbcAnestesiaDescricoes();
			anestesiaDescricoes = getBlocoCirurgicoFacade().buscarAnestesiaDescricoes(crgSeq, seqp); //-C6
			
			MbcDescricaoTecnicas descricaoTecnicas = new MbcDescricaoTecnicas();
			descricaoTecnicas = getBlocoCirurgicoFacade().buscarDescricaoTecnicas(crgSeq, seqp); //-C7
			
			AelProjetoPesquisas projetoPesquisas = new AelProjetoPesquisas();
			projetoPesquisas = getExamesFacade().buscarProjetoPesquisas(crgSeq); //C8
			
			List<MbcDiagnosticoDescricao> diagnosticosDescricoes = new ArrayList<MbcDiagnosticoDescricao>();
			diagnosticosDescricoes = getBlocoCirurgicoFacade().buscarMbcDiagnosticoDescricao(crgSeq, seqp); //-C9
			
			MbcFiguraDescricoes figuraDescricoes = new MbcFiguraDescricoes();
			figuraDescricoes = getBlocoCirurgicoFacade().buscarFiguraDescricoes(crgSeq, seqp); //-C10
			
			MbcImagemDescricoes imagemDescricoes = null;
			if(figuraDescricoes != null){
				imagemDescricoes = new MbcImagemDescricoes();
				imagemDescricoes = getBlocoCirurgicoFacade().buscarImagemDescricoes(crgSeq, seqp, figuraDescricoes.getId().getSeqp()); //C11
			}
				
			MbcDescricaoCirurgica descricaoCirurgicaC12 = new MbcDescricaoCirurgica();
			descricaoCirurgicaC12 = getBlocoCirurgicoFacade().buscarDescricaoCirurgica(crgSeq, seqp); //-C12
			
			MbcNotaAdicionais notaAdicionais = new MbcNotaAdicionais();
			notaAdicionais = getBlocoCirurgicoFacade().buscarNotaAdicionais(crgSeq, seqp); //-C13
	
			MbcNotaAdicionais notaAdicionais2 = null;
			if(notaAdicionais != null){
				notaAdicionais2 = new MbcNotaAdicionais();
				notaAdicionais2 = getBlocoCirurgicoFacade().buscarNotaAdicionais2(notaAdicionais.getId().getDcgCrgSeq(), notaAdicionais.getId().getDcgSeqp(), notaAdicionais.getId().getSeqp()); //C15
			}
			
			MbcNotaAdicionais notaAdicionais1 = null;
			if(notaAdicionais2 != null){
				notaAdicionais1 = new MbcNotaAdicionais();
				notaAdicionais1 = getBlocoCirurgicoFacade().buscarNotaAdicionais1(crgSeq, seqp, notaAdicionais2.getId().getDcgCrgSeq(), notaAdicionais2.getId().getDcgSeqp(), notaAdicionais2.getId().getSeqp()); //C14
			}
			
			//Preenche VO
			descricaoCirurgicaVO.setTitulo(getTitulo(crgSeq, seqp));
			if(descricaoCirurgica.getMbcCirurgias() != null ){
				descricaoCirurgicaVO.setUndDescricao(descricaoCirurgica.getMbcCirurgias().getUnidadeFuncional().getDescricao());
				descricaoCirurgicaVO.setDataCirurgia(descricaoCirurgica.getMbcCirurgias().getData());
				descricaoCirurgicaVO.setNomePac(descricaoCirurgica.getMbcCirurgias().getPaciente().getNome());
				descricaoCirurgicaVO.setProntuario(CoreUtil.formataProntuario(descricaoCirurgica.getMbcCirurgias().getPaciente().getProntuario()));
				descricaoCirurgicaVO.setIdade(getIdade(descricaoCirurgica));
				if (descricaoCirurgica.getMbcCirurgias().getPaciente().getSexo() != null) {
					descricaoCirurgicaVO.setSexo(descricaoCirurgica.getMbcCirurgias().getPaciente().getSexo().getDescricao());
				}
				if(descricaoCirurgica.getMbcCirurgias().getAtendimento() != null && descricaoCirurgica.getMbcCirurgias().getAtendimento().getLeito() != null){
					descricaoCirurgicaVO.setLeito(descricaoCirurgica.getMbcCirurgias().getAtendimento().getLeito().getLeitoID());
				}
				if(descricaoCirurgica.getMbcCirurgias().getConvenioSaude() != null){
					descricaoCirurgicaVO.setConvenio(descricaoCirurgica.getMbcCirurgias().getConvenioSaude().getDescricao());
				}
				descricaoCirurgicaVO.setNomePac1(descricaoCirurgica.getMbcCirurgias().getPaciente().getNome());
				descricaoCirurgicaVO.setLeitoRodape(getLeitoRodape(descricaoCirurgica));
				descricaoCirurgicaVO.setProntuario1(CoreUtil.formataProntuario(descricaoCirurgica.getMbcCirurgias().getPaciente().getProntuario()));
				descricaoCirurgicaVO.setPacCodigo(descricaoCirurgica.getMbcCirurgias().getPaciente().getCodigo());
			}
			if(projetoPesquisas != null){
				descricaoCirurgicaVO.setProjeto(getNomeProjeto(projetoPesquisas));
				descricaoCirurgicaVO.setNomeResp(projetoPesquisas.getNomeResponsavel());
			}
			descricaoCirurgicaVO.setLabelProjeto(getVerificarApresentaLabelProjeto(descricaoCirurgicaVO));
			descricaoCirurgicaVO.setLabelResp(getVerificarApresentaLabelResp(descricaoCirurgicaVO));
			if(especialidade != null){
				descricaoCirurgicaVO.setNomeEsp(especialidade.getNomeEspecialidade());
			}
			if(profDescricoes != null && profDescricoes.size() > 0){
				List<LinhaReportVO> equipes = new ArrayList<LinhaReportVO>();			
				
				Collections.sort(profDescricoes, new Comparator<MbcProfDescricoes>() {
					@Override
					public int compare(MbcProfDescricoes o1, MbcProfDescricoes o2) {
						String nome1 = o1.getServidorProf() != null ? o1.getServidorProf().getPessoaFisica().getNome() : "";
						String nome2 = o2.getServidorProf() != null ? o2.getServidorProf().getPessoaFisica().getNome() : "";
						if (!o1.getTipoAtuacao().getOrder().equals(o2.getTipoAtuacao().getOrder())) {
							return o1.getTipoAtuacao().getOrder().compareTo(o2.getTipoAtuacao().getOrder());
						}else if (!nome1.equalsIgnoreCase(nome2)) {
							return nome1.compareTo(nome2);
						}else{
							return 0;
						}
					}
				});

				String tipoAtuacao;
				String tipoAtuacaoOld = null;
				
				for (MbcProfDescricoes mbcProfDescricoes : profDescricoes) {
					LinhaReportVO equipe = new LinhaReportVO();
					
					tipoAtuacao = getTipoAtuacao(mbcProfDescricoes);
					if(tipoAtuacao.equals(tipoAtuacaoOld)){
						equipe.setTexto1("");
					}else{
						equipe.setTexto1(tipoAtuacao);
					}
					
					String texto2 = getNome1(mbcProfDescricoes);
					if(texto2.isEmpty()){
						equipe.setTexto2(null);
					}else{
						equipe.setTexto2(texto2);
					}
					equipe.setTexto3(getConselho(mbcProfDescricoes));
					
					tipoAtuacaoOld = tipoAtuacao;
					equipes.add(equipe);
				}
				descricaoCirurgicaVO.setEquipeList(equipes);
			}
			if(descricaoItens != null){
				descricaoCirurgicaVO.setDescricaoAsa(getDescricaoAsa(descricaoItens));
				descricaoCirurgicaVO.setDtHrInicioCirurgia(descricaoItens.getDthrInicioCirg());
				descricaoCirurgicaVO.setDtHrFimCirurgia(descricaoItens.getDthrFimCirg());
				if(descricaoItens.getCarater() != null){
					descricaoCirurgicaVO.setCarater(descricaoItens.getCarater().getDescricao());
				}
				descricaoCirurgicaVO.setObservacao(descricaoItens.getObservacao());
				descricaoCirurgicaVO.setAchadosOperatorios(descricaoItens.getAchadosOperatorios());
				if(descricaoItens.getIndIntercorrencia() != null && descricaoItens.getIndIntercorrencia() && descricaoItens.getIntercorrenciaClinica()!=null){
					descricaoCirurgicaVO.setIntercorrenciaClinica("Intercorrências: " + descricaoItens.getIntercorrenciaClinica());
				}else{
					descricaoCirurgicaVO.setIntercorrenciaClinica("Não houve intercorrências durante o procedimento cirúrgico.");
				}
				if(descricaoItens.getIndPerdaSangue() != null && descricaoItens.getIndPerdaSangue() && descricaoItens.getVolumePerdaSangue()!=null){
					descricaoCirurgicaVO.setPerdaSangue("Houve perda sanguínea intra-operatória estimada em "+descricaoItens.getVolumePerdaSangue()+"ml.");
				}else{
					descricaoCirurgicaVO.setPerdaSangue("Não houve perda sanguínea intra-operatória significativa.");
				}
				
			}
			if(diagnosticosDescricoes != null){
				List<LinhaReportVO> diagnosticos = new ArrayList<LinhaReportVO>();
				
				for (MbcDiagnosticoDescricao diagnosticoDescricao : diagnosticosDescricoes) {
					LinhaReportVO diagnostico = new LinhaReportVO();
					
					diagnostico.setTexto1(diagnosticoDescricao.getId().getClassificacao().getDescricao());
					diagnostico.setTexto2(diagnosticoDescricao.getCid().getCodigo());
					diagnostico.setTexto3(getDescricaoCidCompleta(diagnosticoDescricao));
					if("S".equals(diagnosticoDescricao.getDestacar())){
						diagnostico.setBool(true);
					}else{
						diagnostico.setBool(false);
					}
					
					diagnosticos.add(diagnostico);
				}
				descricaoCirurgicaVO.setDiagnosticoList(diagnosticos);
			}

			if(anestesiaDescricoes != null){
				descricaoCirurgicaVO.setDescricaoAnestesia(anestesiaDescricoes.getTipoAnestesia().getDescricao());
			}
			if(descricaoTecnicas != null){
				descricaoCirurgicaVO.setDescricaoTecnica(descricaoTecnicas.getDescricaoTecnica());
			}
			descricaoCirurgicaVO.setNumeroDesenho(getNumeroDesenho(crgSeq, seqp));
			descricaoCirurgicaVO.setCamposDesenho(getVisualizarCamposDesenho(crgSeq, seqp));
			if(figuraDescricoes != null){
				descricaoCirurgicaVO.setTexto(figuraDescricoes.getTexto());
			}
			if(imagemDescricoes != null){
				descricaoCirurgicaVO.setImagem(imagemDescricoes.getImagem());			
			}
			if(descricaoCirurgicaC12 != null){
				descricaoCirurgicaVO.setDataConclui(getDataConclui(descricaoCirurgicaC12));
				descricaoCirurgicaVO.setResponsavel(getResponsavel(descricaoCirurgicaC12));
			}
			descricaoCirurgicaVO.setNumeroNotasAdicionais(getNumeroNotasAdicionais(crgSeq, seqp));
			descricaoCirurgicaVO.setCamposNotasAdicionais(getVisualizarCamposNotasAdicionas(crgSeq, seqp));
			if(notaAdicionais != null){
				descricaoCirurgicaVO.setNotasAdicionais(notaAdicionais.getNotasAdicionais());
				descricaoCirurgicaVO.setEspProf(getEspProf(notaAdicionais));
			}
			if(notaAdicionais2 != null){
				descricaoCirurgicaVO.setCamposIdentificacao(getVisualizarCamposIdentificacao(notaAdicionais2));
			}
			if(notaAdicionais1 != null){
				descricaoCirurgicaVO.setCriadoEm(notaAdicionais1.getCriadoEm());
				descricaoCirurgicaVO.setResponsavel1(getResponsavel1(notaAdicionais1));
			}
			
			String path = contextPath + "/" + getParametroFacade().recuperarCaminhoLogo();
			descricaoCirurgicaVO.setCaminhoLogo(path);
			descricaoCirurgicaVO.setCurrentDate(new Date());
			if(previa == null){
				descricaoCirurgicaVO.setPrevia(false);
			}else{
				descricaoCirurgicaVO.setPrevia(previa);
			}
			
			descricoesCirurgicas.add(descricaoCirurgicaVO);
			
			if(descricoesCirurgicas.isEmpty()){
				throw new ApplicationBusinessException(ConsultaNotasPolONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
			}
		}
		return descricoesCirurgicas;
	}

	private String getTipoAtuacao(MbcProfDescricoes mbcProfDescricoes) {
		String retorno;
		retorno = mbcProfDescricoes.getTipoAtuacao().getDescricaoProf() + ": ";
		return retorno;
	}

	private String getLeitoRodape(MbcDescricaoCirurgica descricaoCirurgica) {
		String retorno = null;
		if(descricaoCirurgica.getMbcCirurgias().getAtendimento() != null){
			retorno = getConsultaDescricaoCirurgicaPolRN().buscarLeitoRodape(descricaoCirurgica.getMbcCirurgias().getAtendimento());
		}
		return retorno;
	}
	
	private String getResponsavel1(MbcNotaAdicionais notaAdicionais1) throws BaseException {
		String retorno = getConsultaDescricaoCirurgicaPolRN().buscarResponsavel(notaAdicionais1.getServidor().getId().getVinCodigo(), notaAdicionais1.getServidor().getId().getMatricula());
		return retorno;
	}

	private Boolean getVisualizarCamposIdentificacao(MbcNotaAdicionais notaAdicionais2) {
		Integer matricula = getConsultaDescricaoCirurgicaPolRN().verificarMatriculaNTA(notaAdicionais2.getServidor().getId().getMatricula());
			
		Boolean retorno = getConsultaDescricaoCirurgicaPolRN().verificarM6(matricula);
		
		return retorno;
	}

	private String getEspProf(MbcNotaAdicionais notaAdicionais) {
		String retorno = getPrescricaoMedicaFacade().obtemNomeServidorEditado(notaAdicionais.getServidor().getId().getVinCodigo(), notaAdicionais.getServidor().getId().getMatricula());
		retorno = getAmbulatorioFacade().obterDescricaoCidCapitalizada(retorno);
		return retorno;
	}

	private Boolean getVisualizarCamposNotasAdicionas(Integer crgSeq, Short seqp) {
		Boolean retorno = getConsultaDescricaoCirurgicaPolRN().verificarBoolNTA(crgSeq, seqp);
		return retorno;
	}

	private String getNumeroNotasAdicionais(Integer crgSeq, Short seqp) {
		String retorno = null;
		if(getNumeroDesenho(crgSeq, seqp) != null){
			retorno = getConsultaDescricaoCirurgicaPolRN().buscarNumNotasAdicionais(crgSeq, seqp, 7);
		}else{
			retorno = getConsultaDescricaoCirurgicaPolRN().buscarNumNotasAdicionais(crgSeq, seqp, 6);
		}
		return retorno;
	}

	private String getResponsavel(MbcDescricaoCirurgica descricaoCirurgica) throws BaseException {
		String retorno = getConsultaDescricaoCirurgicaPolRN().buscarResponsavel(descricaoCirurgica.getServidor().getId().getVinCodigo(), descricaoCirurgica.getServidor().getId().getMatricula());
		return retorno;
	}

	private Date getDataConclui(MbcDescricaoCirurgica descricaoCirurgica) {
		Date retorno;
		if(descricaoCirurgica.getDthrConclusao() != null){
			retorno = descricaoCirurgica.getDthrConclusao();
		}else{
			retorno = descricaoCirurgica.getCriadoEm();
		}
	
		return retorno;
	}

	private Boolean getVisualizarCamposDesenho(Integer crgSeq, Short seqp) {
		Boolean retorno = getConsultaDescricaoCirurgicaPolRN().verificarBoolFDC(crgSeq, seqp);
		return retorno;
	}

	private String getNumeroDesenho(Integer crgSeq, Short seqp) {
		String retorno = getConsultaDescricaoCirurgicaPolRN().buscarNumDesenho(crgSeq, seqp, 6);
		return retorno;
	}
	

	private String getDescricaoCidCompleta(MbcDiagnosticoDescricao diagnosticoDescricao) {
		String retorno;
		
		if(diagnosticoDescricao.getComplemento() != null){
			retorno = diagnosticoDescricao.getCid().getDescricao() + " " + diagnosticoDescricao.getComplemento();
		}else{
			retorno = diagnosticoDescricao.getCid().getDescricao();
		}		
		return retorno;
	}

	private String getConselho(MbcProfDescricoes profDescricoes) throws ApplicationBusinessException {
		String retorno = profDescricoes.getServidorProf() != null ? getConsultaDescricaoCirurgicaPolRN().buscarConselho(
				profDescricoes.getServidorProf().getId().getVinCodigo(),
				profDescricoes.getServidorProf().getId().getMatricula()) : null;
		if(retorno == null){
			retorno = "";
		}
		return retorno;
	}

	private String getDescricaoAsa(MbcDescricaoItens descricaoItens) {
		if (descricaoItens != null && descricaoItens.getAsa() != null){
			return (descricaoItens.getAsa().getDescricao());
		}
		return null;
	}

	private Boolean getVerificarApresentaLabelProjeto(DescricaoCirurgiaVO descricaoCirurgicaVO) {
		Boolean retorno = null;
		if(descricaoCirurgicaVO.getProjeto() != null){
			retorno = getConsultaDescricaoCirurgicaPolRN().verificarApresentaLabelProjeto(descricaoCirurgicaVO.getProjeto());
		}
		return retorno;
	}
	
	private Boolean getVerificarApresentaLabelResp(DescricaoCirurgiaVO descricaoCirurgicaVO) {
		Boolean retorno = null;
		if(descricaoCirurgicaVO.getNomeResp() != null){
			retorno = getConsultaDescricaoCirurgicaPolRN().verificarApresentaLabelResp(descricaoCirurgicaVO.getNomeResp());
		}
		return retorno;
	}

	protected ConsultaDescricaoCirurgicaPolRN getConsultaDescricaoCirurgicaPolRN() {
		return consultaDescricaoCirurgicaPolRN;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
		
	protected String getNomeProjeto(AelProjetoPesquisas projetoPesquisas){
		String retorno = projetoPesquisas.getNumero()+"-"+projetoPesquisas.getNome();
			
		return retorno;		
	}
	
	protected String getTitulo(Integer crgSeq, Short seqp){
		String retorno;
		if(getConsultaDescricaoCirurgicaPolRN().verificarApresentaTitulo(crgSeq, seqp)){
			retorno = "* * *   COM  NOTAS  ADICIONAIS   * * *";
		}else{
			retorno = "";
		}
		return retorno;
	}
	
	protected String getNome1(MbcProfDescricoes profDescricoes){
		String retorno = null;
		RapServidores rapServidor = new RapServidores();
		if(profDescricoes.getServidorProf() != null){
			rapServidor = profDescricoes.getServidorProf();
			getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						profDescricoes.getServidorProf().getId().getMatricula(),
						profDescricoes.getServidorProf().getId().getVinCodigo());
		}
		if(rapServidor != null && rapServidor.getPessoaFisica() != null){
			retorno = rapServidor.getPessoaFisica().getNome();
		}

		
		//Se profissionais forem auxiliares
		if(retorno == null){
			if(profDescricoes.getNome() != null && profDescricoes.getCategoria() != null){
				retorno = profDescricoes.getNome() +"    Função: "+profDescricoes.getCategoria().getDescricao();
			}
		}
		
		if(retorno == null){
			retorno = "";
		}
		return retorno;		
	}
	
	protected String getIdade(MbcDescricaoCirurgica descricaoCirurgica){
		String retorno = getConsultaDescricaoCirurgicaPolRN().buscarIdade(descricaoCirurgica.getMbcCirurgias().getPaciente().getDtNascimento(), descricaoCirurgica.getMbcCirurgias().getData());
		
		return retorno;
	}
		
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
