package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioFichasDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioNodoPOL;
import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghNodoPol;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidade;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidadeHist;
import br.gov.mec.aghu.model.VAipPolMdtosAghuHist;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.DocumentoGEDVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.IDigitalizacaoPOLFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDAdministrativosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDAtivosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class AghNodoPolON extends BaseBusiness {

	private static final String DOCUMENTOS_DIGITALIZADOS = "Documentos Digitalizados", PROCESSOS_ADMINISTRATIVOS ="Processos Administrativos";
	private static final String DT_EXAME = "dtExame";
	private static final String UNF_SEQ = "unfSeq";
	private static final Log LOG = LogFactory.getLog(AghNodoPolON.class);
	private static final long serialVersionUID = -7075741389016508313L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IDigitalizacaoPOLFacade digitalizacaoPOLFacade;

	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private enum AghNodoPolONExceptionCode implements BusinessExceptionCode {
		ERRO_GERAR_NOS_CONSULTAS_AMBULATORIAIS
	}

	// ------[MONTAR A LISTA PARA A ÁRVORE]
	public List<NodoPOLVO> montarListaPOL(Integer prontuario, Integer codigo, List<AghNodoPol> nodoOriginalList, Map<ParametrosPOLEnum, Object> parametros, Boolean obito) throws ApplicationBusinessException {
		if (prontuario == null) {
			throw new ApplicationBusinessException("MENSAGEM_NENHUM_PACIENTE_ENCONTRATO", Severity.ERROR);
		}
		List<NodoPOLVO> nodoPolList = new ArrayList<>();
		boolean hasHist = prontuarioOnlineFacade.verificarExibicaoNodoDadoHistorico(codigo);		
		final Boolean paramAtivaDigitalizacaoPOL = (Boolean) parametros.get(ParametrosPOLEnum.ATIVA_DIGITALIZACAO_POL);
		final Boolean acessoDocsDigitalizadosInativosPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_INATIVOS);
		
		int idx = 0;
		for (AghNodoPol pol : nodoOriginalList) {

			NodoPOLVO vo = null;
			DominioNodoPOL dominioPOL = DominioNodoPOL.createByTipo(pol.getNome());

			
			switch (dominioPOL) {
			case DIGITALIZACAO_DOCS_LEGAIS:
				//Monta nodo documentos digitalizados
				if(!paramAtivaDigitalizacaoPOL){
					continue;
				}
				break;
			case INFORMACOES_PERINATAIS:
				if (perinatologiaFacade.verificarExisteRecemNascido(codigo)) {
					nodoPolList.add(processarNodoInformacoesPerinatais(codigo,prontuario));				    
				}
				 break;
				 
			case HISTORIA_OBSTETRICA:
				if (perinatologiaFacade.verificarExisteGestacao(codigo)) {
					nodoPolList.add(processarNodoHistoricaObstetrica(codigo,prontuario));				    
				}
				break;
			case DOCUMENTOS_CERTIFICADOS:
				if (prontuario != null
						&& certificacaoDigitalFacade.verificarExisteDocumentosPacienteProntuario(prontuario)) {
					 nodoPolList.add(processarNodoDocumentosCertificados(prontuario, parametros, pol));
			}
				break;
			case DADOS_HISTORICOS:
				if(!hasHist){
					break;
				}
			default:
			String icone = pol.getIcone();
			if (dominioPOL.equals(DominioNodoPOL.DADOS_PACIENTE) && obito){
				icone = "/images/icons/obito.png";	
			}
			
			String page = null;
			if (dominioPOL.isOpenPage()) {
				page = "pol-" + pol.getNome();
			}
				// ----[AJUSTA A BASE DE DADOS DO AGHU 5]
			if (icone != null) {
				icone = icone.replace("/images/icons/", "/resources/img/icons/");
			}
			vo = new NodoPOLVO(prontuario, pol.getNome(), pol.getDescricao(), icone, idx++, page);
			NodoPOLVO nenhumRegistro = new NodoPOLVO(prontuario, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO_ICONE, true);
			nenhumRegistro.addParam(NodoPOLVO.COD_PACIENTE, codigo);
				
			gerarProcessosDigitais(paramAtivaDigitalizacaoPOL, acessoDocsDigitalizadosInativosPOL, pol, vo, dominioPOL);

			
			// --[VERIFICA NODOS COM FILHOS]--
			if (dominioPOL.hasFilhos()) {
				vo.addNodos(nenhumRegistro);
			}

			vo.addParam(NodoPOLVO.COD_PACIENTE, codigo);
			nodoPolList.add(vo);
				break;
		}

		}
		return nodoPolList;
	}

	private NodoPOLVO processarNodoDocumentosCertificados(Integer prontuario,
			Map<ParametrosPOLEnum, Object> parametros, AghNodoPol pol) {
		// caso não tenha permissão para pesquisar apenas apresenta a lista de
		// documentos do paciente
		if (!(Boolean) parametros.get(ParametrosPOLEnum.DOCUMENTOS_CERTIFICADOS_POL)) {
			return new NodoPOLVO(prontuario, DominioNodoPOL.DOCUMENTOS_CERTIFICADOS.getTipo(), pol.getDescricao(),
					PolConstants.IMAGES_ICON_CERTIFICACAO_DIGITAL, PolConstants.CONSULTAR_DOCUMENTOS_CERTIFICADOS_XHTML);
			// caso contrário, permite que o usuário informe os filtros da
			// pesquisa
		} else {
			return new NodoPOLVO(prontuario, DominioNodoPOL.DOCUMENTOS_CERTIFICADOS.getTipo(), pol.getDescricao(),
					PolConstants.IMAGES_ICON_CERTIFICACAO_DIGITAL,
					PolConstants.PESQUISAR_DOCUMENTOS_CERTIFICADOS_PACIENTE_XHTML);
		}
	}

	private void gerarProcessosDigitais(
			final Boolean paramAtivaDigitalizacaoPOL,
			final Boolean acessoDocsDigitalizadosInativosPOL, AghNodoPol pol,
			NodoPOLVO vo, DominioNodoPOL dominioPOL) {
		
		if(paramAtivaDigitalizacaoPOL && acessoDocsDigitalizadosInativosPOL && DominioSituacao.A == pol.getStatus()){
			
			if(dominioPOL == DominioNodoPOL.DIGITALIZACAO_DOCS_LEGAIS){
				setarParametrosNodoDigitalizacao( vo, DominioOrigemDocsDigitalizados.DOC_LGL, 
												 ((Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
												 DominioFichasDocsDigitalizados.ATIVOS);
				
			} else if(dominioPOL == DominioNodoPOL.PRONTUARIO_PAPEL){
				setarParametrosNodoDigitalizacao( vo, null, 
												  ((Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
												  DominioFichasDocsDigitalizados.INATIVOS);
			}
		}
	}

	// ------[BUSCA NODOS FILHOS]
	public void expandirNodosPOL(NodoPOLVO polvo, Map<ParametrosPOLEnum, Object> parametros) throws ApplicationBusinessException {
		DominioNodoPOL dominioPOL = DominioNodoPOL.createByTipo(polvo.getTipo());

		if (dominioPOL != null && dominioPOL.hasFilhos() && polvo.getNodos().size() <= 1) {

			final Boolean paramAtivaDigitalizacaoPOL = (Boolean) parametros.get(ParametrosPOLEnum.ATIVA_DIGITALIZACAO_POL);
			final Boolean acessoDocsDigitalizadosInativosPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_INATIVOS);

			if (DominioNodoPOL.INTERNACAO.equals(dominioPOL)) {
				final boolean acessoDocsDigitalizadosIntPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_INT_POL);
				montarListaInternacao(polvo, DominioNodoPOL.DETALHE_INTERNACAO.getTipo(), paramAtivaDigitalizacaoPOL, acessoDocsDigitalizadosIntPOL);

				// ---[DETALHE AMBULATORIO]
			} else if (DominioNodoPOL.AMBULATORIO.equals(dominioPOL)) {
				final boolean acessoDocsDigitalizadosAmbPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_AMB_POL);
				montarListaAmbulatorio(polvo, polvo.getIcone(), paramAtivaDigitalizacaoPOL, acessoDocsDigitalizadosAmbPOL);

				// ---[DETALHE EMERGÊNCIA]
			} else if (DominioNodoPOL.EMERGENCIA.equals(dominioPOL)) {
				final boolean acessoDocsDigitalizadosEmePOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_EME_POL);
				montarListaEmergencia(polvo, paramAtivaDigitalizacaoPOL, acessoDocsDigitalizadosEmePOL);

				// ---[DETALHE EXAMES]
			} else if (DominioNodoPOL.EXAME.equals(dominioPOL)) {
				montarListaExames(polvo, (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ADMIN_POL), (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_VER_FLUXOGRAMA));

				final boolean acessoDocsDigitalizadosExaPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_DOCS_DIGITALIZADOS_EXA_POL);
				if (paramAtivaDigitalizacaoPOL && acessoDocsDigitalizadosExaPOL) {
					montaNodoDocumentosDigitalizados(polvo, DominioOrigemDocsDigitalizados.EXE);
				}

			} else if (DominioNodoPOL.EXAME_AMOSTRAS_COLETADAS.equals(dominioPOL)) {
				gerarSubArvoreAmostrasColetadasItens(polvo, false);

			} else if (DominioNodoPOL.EXAME_LABORATORIOS_SERVICOS.equals(dominioPOL)) {
				gerarSubArvoreLaboratorioItens(polvo, false);

			} else if (DominioNodoPOL.EXAME_ORDEM_CRONOLOGICA.equals(dominioPOL)) {
				gerarSubArvoreOrdemCronologicaItens(polvo, false);

			} else if (DominioNodoPOL.MEDICAMENTOS.equals(dominioPOL)) {
				montarListaMedicamentos(polvo, (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ADMIN_POL));

			} else if (DominioNodoPOL.SESSAO_TERAPEUTICA.equals(dominioPOL)) {
				montarListaSessaoTerapeutica(polvo);

			} else if (DominioNodoPOL.DADOS_HISTORICOS.equals(dominioPOL)) {
				montarListaDadosHistorico(polvo);

			} else if (DominioNodoPOL.EXAME_HIST.equals(dominioPOL)) {
				montarListaExamesHist(polvo, (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ADMIN_POL), (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_VER_FLUXOGRAMA));

			} else if (DominioNodoPOL.EXAME_AMOSTRAS_COLETADAS_HIST.equals(dominioPOL)) {
				gerarSubArvoreAmostrasColetadasItens(polvo, true);

			} else if (DominioNodoPOL.EXAME_LABORATORIOS_SERVICOS_HIST.equals(dominioPOL)) {
				gerarSubArvoreLaboratorioItens(polvo, true);

			} else if (DominioNodoPOL.EXAME_ORDEM_CRONOLOGICA_HIST.equals(dominioPOL)) {
				gerarSubArvoreOrdemCronologicaItens(polvo, true);

			} else if (DominioNodoPOL.MEDICAMENTOS_HIST.equals(dominioPOL)) {
				montarListaMedicamentosHist(polvo, (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ADMIN_POL));

			} else if (paramAtivaDigitalizacaoPOL && acessoDocsDigitalizadosInativosPOL && 
						dominioPOL.equals(DominioNodoPOL.DIGITALIZACAO_DOCS_LEGAIS)) {

				NodoPOLVO nodoDigitalizacaoDocsLegais = new NodoPOLVO( polvo.getProntuario(), DominioNodoPOL.DIGITALIZACAO_DOCS_LEGAIS.getTipo(), 
																       polvo.getDescricao(), PolConstants.IMAGES_ICONS_CD,
																       PolConstants.CONSULTAR_DOCS_DIGITALIZACAO_XHTML);

				setarParametrosNodoDigitalizacao( nodoDigitalizacaoDocsLegais, DominioOrigemDocsDigitalizados.DOC_LGL, 
												 ((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
												 DominioFichasDocsDigitalizados.ATIVOS);
				
				polvo.getNodos().add(nodoDigitalizacaoDocsLegais);
				
		
			} else if (paramAtivaDigitalizacaoPOL && acessoDocsDigitalizadosInativosPOL && 
						DominioNodoPOL.PRONTUARIO_PAPEL.equals(dominioPOL)) {

				NodoPOLVO nodoDigitalizacaoInativos = new NodoPOLVO(polvo.getProntuario(),DominioNodoPOL.PRONTUARIO_PAPEL.getTipo(), 
																	polvo.getDescricao(), PolConstants.IMAGES_ICONS_CD,
																    PolConstants.CONSULTAR_DOCS_DIGITALIZACAO_XHTML);

				setarParametrosNodoDigitalizacao( nodoDigitalizacaoInativos, null, 
												 ((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
												 DominioFichasDocsDigitalizados.INATIVOS);
				
				polvo.getNodos().add(nodoDigitalizacaoInativos);
			}

			// ---[AJUSTE]
			if (polvo.getNodos().size() > 1) {
				polvo.getNodos().get(0).setAtivo(false);
			}
		}
	}

	// ------[MONTAR A LISTA PARA A ÁRVORE - INTERNACAO]
	public void montarListaInternacao(NodoPOLVO vo, String tipo, final Boolean paramAtivaDigitalizacaoPOL, final Boolean acessoDocsDigitalizadosIntPOL) throws ApplicationBusinessException {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		int idx = 0;
		for (InternacaoVO internacao : prontuarioOnlineFacade.pesquisaInternacoes(vo.getProntuario())) {
			String descricao = null;
			if (internacao.getDthrInicio() != null && internacao.getDthrFim() != null) {
				descricao = df.format(internacao.getDthrInicio()) + " - " + df.format(internacao.getDthrFim());
			} else if (internacao.getDthrInicio() != null) {
				descricao = df.format(internacao.getDthrInicio());
			} else if (internacao.getDthrFim() != null) {
				descricao = df.format(internacao.getDthrFim());
			}
			NodoPOLVO child = new NodoPOLVO(vo.getProntuario(), tipo, descricao, PolConstants.IMAGES_ICONS_CLOCK, idx++, "pol-" + tipo);
			child.addParam("dtInicio", internacao.getDthrInicio());
			child.addParam("dtFim", internacao.getDthrFim());
			child.addParam("tipo", internacao.getTipo());
			child.addParam("seq", internacao.getSeq());
			child.addParam(NodoPOLVO.COD_PACIENTE, internacao.getCodigoPaciente());
			vo.addNodos(child);
		}

		if (Boolean.TRUE.equals(paramAtivaDigitalizacaoPOL) && Boolean.TRUE.equals(acessoDocsDigitalizadosIntPOL)) {
			montaNodoDocumentosDigitalizados(vo, DominioOrigemDocsDigitalizados.INT);
			montaNodoDocumentosDigitalizadosAdministrativos(vo, DominioOrigemDocsDigitalizados.INT);
		}

	}

	@SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "PMD.ExcessiveMethodLength" })
	// ------[MONTAR A LISTA PARA A ÁRVORE - AMBULATÓRIO]
	public void montarListaAmbulatorio(NodoPOLVO vo, String icone, final Boolean paramAtivaDigitalizacaoPOL, final Boolean acessoDocsDigitalizadosAmbPOL) throws ApplicationBusinessException {

		Integer codPaciente = (Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE);
		
		NodoPOLVO nodoCronologico = new NodoPOLVO(vo.getProntuario(), "amb_cronologico", "Cronológica", PolConstants.IMAGES_ICONS_CLOCK, null, null);
		nodoCronologico.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

		NodoPOLVO nodoEspecialidade = new NodoPOLVO(vo.getProntuario(), "amb_especialidades", "Especialidade", PolConstants.IMAGES_ICONS_ESPECIALIDADES_MEDICAS, null, null);
		nodoEspecialidade.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		
		NodoPOLVO nodoAltasAmbulatoriais = new NodoPOLVO(vo.getProntuario(), "altasAmbulatoriais", "Altas Ambulatoriais", PolConstants.IMAGES_ICONS_ALTAS_AMBULATORIAIS, null,
				PolConstants.ALTAS_AMBULATORIAIS_XHTML);
		nodoAltasAmbulatoriais.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

		NodoPOLVO nodoConsultoriaAmbulatorial = new NodoPOLVO(vo.getProntuario(), "consultoriasAmbulatoriais", PolConstants.CONSULTORIA_AMBULATORIAL,
				PolConstants.IMAGES_ICONS_CONSULTORIA_AMBULATORIAL, PolConstants.EXIBE_CONSULTORIA_AMBULATORIAL_XHTML);
		nodoConsultoriaAmbulatorial.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		
		vo.addNodos(nodoConsultoriaAmbulatorial);
		vo.addNodos(nodoCronologico);
		vo.addNodos(nodoEspecialidade);

		boolean existeAtendimentoAmbCronologico = this.aghuFacade.verificarExisteAtendimentoAmbCronologicoPorPacCodigo(codPaciente);

		if (existeAtendimentoAmbCronologico == false) {
			List<NodoPOLVO> listAmbCrono = new ArrayList<NodoPOLVO>(0);
			listAmbCrono.add(new NodoPOLVO(vo.getProntuario(), PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO_ICONE, true));
			nodoCronologico.setNodos(listAmbCrono);

			List<NodoPOLVO> listAmbEsp = new ArrayList<NodoPOLVO>(0);
			listAmbEsp.add(new NodoPOLVO(vo.getProntuario(), PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO_ICONE, true));
			nodoEspecialidade.setNodos(listAmbEsp);

		} else {
			List<AghAtendimentos> atendimentos = this.aghuFacade.pesquisarAtendimentoAmbCronologicoPorPacCodigo(codPaciente);
			List<AghEspecialidades> especialidades = this.gerarEstruturaDatasArvoreAmb("cronologico", atendimentos, nodoCronologico, vo.getProntuario(), codPaciente);

			if (!especialidades.isEmpty()) {
				ComparatorChain chainSorter = new ComparatorChain();
				BeanComparator<String> especialidadeSorter = new BeanComparator<>("nomeEspecialidade", new NullComparator(false));
				chainSorter.addComparator(especialidadeSorter);
				Collections.sort(especialidades, chainSorter);
			}

			for (AghEspecialidades especialidade : especialidades) {
				NodoPOLVO nodoTipoEspecialidade = new NodoPOLVO(vo.getProntuario(), "amb_especialidade_nome_" + especialidade.getNomeEspecialidade(), especialidade.getNomeEspecialidade(),
						PolConstants.IMAGES_ICONS_ESPECIALIDADES_MEDICAS_2, null, null);
				nodoTipoEspecialidade.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

				nodoTipoEspecialidade.setQuebraLinha(true);
				List<AghAtendimentos> filtrados = (List<AghAtendimentos>) CollectionUtils.select(atendimentos, new EspecialidadePredicate(especialidade));
				this.gerarEstruturaDatasArvoreAmb("especialidade", filtrados, nodoTipoEspecialidade, vo.getProntuario(), codPaciente);
				nodoEspecialidade.addNodos(nodoTipoEspecialidade);
			}

		}

		// -------------- NODO ALTAS AMBULATORIAIS
		if (ambulatorioFacade.verificarExibicaoNodoAltasAmbulatoriais(codPaciente)) {
			vo.addNodos(nodoAltasAmbulatoriais);
		}

		if (Boolean.TRUE.equals(paramAtivaDigitalizacaoPOL) && Boolean.TRUE.equals(acessoDocsDigitalizadosAmbPOL)) {
			montaNodoDocumentosDigitalizados(vo, DominioOrigemDocsDigitalizados.AMB);
			montaNodoDocumentosDigitalizadosAdministrativos(vo, DominioOrigemDocsDigitalizados.AMB);
		}
	}

	@SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<AghEspecialidades> gerarEstruturaDatasArvoreAmb(String prefixo, List<AghAtendimentos> atendimentos, NodoPOLVO nodoPai, Integer prontuario, Integer codPaciente) throws ApplicationBusinessException {
		Date[] dtIni_Per = new Date[3];
		Date[] dtFim_Per = new Date[3];
		String[] desc_Per = new String[3];
		NodoPOLVO[] nodosDatas = new NodoPOLVO[3];
		final Integer numPeriodos = 3;
		Date data;
		Integer mes, ano;

		data = new Date();
		ano = Integer.valueOf((new SimpleDateFormat("yyyy")).format(data));
		mes = Integer.valueOf((new SimpleDateFormat("MM")).format(data));

		try {

			dtFim_Per[0] = DateUtils.parseDate(StringUtils.leftPad(mes.toString(), 2, "0") + ano, new String[] { PolConstants.MASK_M_MYYYY });

			if (mes <= 5) {
				ano -= 1;
				mes += 7;
			} else {
				mes -= 5;
			}

			dtIni_Per[0] = DateUtils.parseDate(StringUtils.leftPad(mes.toString(), 2, "0") + ano, new String[] { PolConstants.MASK_M_MYYYY });

			if (mes == 1) {
				ano -= 1;
				mes = 12;
			} else {
				mes -= 1;
			}

			dtFim_Per[1] = DateUtils.parseDate(StringUtils.leftPad(mes.toString(), 2, "0") + ano, new String[] { PolConstants.MASK_M_MYYYY });

			if (mes <= 5) {
				ano -= 1;
				mes += 7;
			} else {
				mes -= 5;
			}

			dtIni_Per[1] = DateUtils.parseDate(StringUtils.leftPad(mes.toString(), 2, "0") + ano, new String[] { PolConstants.MASK_M_MYYYY });

			if (mes == 1) {
				ano -= 1;
				mes = 12;
			} else {
				mes -= 1;
			}

			dtFim_Per[2] = DateUtils.parseDate(StringUtils.leftPad(mes.toString(), 2, "0") + ano, new String[] { PolConstants.MASK_M_MYYYY });
			dtIni_Per[2] = DateUtils.parseDate("011900", new String[] { PolConstants.MASK_M_MYYYY });

		} catch (ParseException e) {
			throw new ApplicationBusinessException(AghNodoPolONExceptionCode.ERRO_GERAR_NOS_CONSULTAS_AMBULATORIAIS);
		}

		Locale locale = new Locale("pt", "BR");
		SimpleDateFormat mesFormat = new SimpleDateFormat(PolConstants.MASK_MMMM_YYYY, locale);
		for (int i = 0; i < 3; i++) {
			if (i == numPeriodos.intValue() - 1) {
				desc_Per[i] = " até " + mesFormat.format(dtFim_Per[i]);
			} else {
				desc_Per[i] = mesFormat.format(dtIni_Per[i]) + " até " + mesFormat.format(dtFim_Per[i]);
			}
			NodoPOLVO polvo = new NodoPOLVO(prontuario, desc_Per[i] + "_" + prefixo, desc_Per[i], PolConstants.IMAGES_ICONS_TIME, PolConstants.CONSULTA_AMBULATORIO_XHTML);
			polvo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			nodosDatas[i] = polvo; 

		}
		Map<RapServidoresId, String> servidoresLocalCache = new HashMap<>();
		List<AghEspecialidades> especialidades = new ArrayList<AghEspecialidades>();

		for (AghAtendimentos atendimento : atendimentos) {

			if (!especialidades.contains(atendimento.getEspecialidadeAtendimento())) {
				especialidades.add(atendimento.getEspecialidadeAtendimento());
			}

			RapServidores rapServidoresAux = atendimento.getConsulta().getGradeAgendamenConsulta().getProfServidor() != null ? atendimento.getConsulta().getGradeAgendamenConsulta().getProfServidor()
					: atendimento.getConsulta().getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel();

			RapServidoresId RapServidoresIdAux = new RapServidoresId(rapServidoresAux.getId().getMatricula(), rapServidoresAux.getId().getVinCodigo());

			String servidorNodoPolVO = null;
			if (servidoresLocalCache.containsKey(RapServidoresIdAux)) {
				servidorNodoPolVO = servidoresLocalCache.get(RapServidoresIdAux);
			} else {
				servidorNodoPolVO = (String) this.prescricaoMedicaFacade.buscaConsProf(rapServidoresAux)[1];
				servidoresLocalCache.put(RapServidoresIdAux, servidorNodoPolVO);
			}

			NodoPOLVO nodoDataAmbulatorio = new NodoPOLVO(prontuario, DateUtil.obterDataFormatada(atendimento.getDthrInicioTruncada(), "ddMMyyyy") + "_" + prefixo, DateUtil.obterDataFormatada(
					atendimento.getDthrInicioTruncada(), PolConstants.MASK_DATA) + " - " + atendimento.getEspecialidadeAtendimento().getSigla() + " - " + servidorNodoPolVO,
					PolConstants.IMAGES_ICONS_AMBULATORIO, PolConstants.CONSULTA_AMBULATORIO_XHTML);
			nodoDataAmbulatorio.addParam(PolConstants.ATENDIMENTOS, "," + atendimento.getSeq().toString());
			nodoDataAmbulatorio.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			nodoDataAmbulatorio.setQuebraLinha(true);

			for (int i = 0; i < 3; i++) {
				if (DateUtil.entreTruncado(atendimento.getDthrInicioTruncada(), dtIni_Per[i], getDataUltimoDiaMes(dtFim_Per[i]))) {
					nodosDatas[i].addNodos(nodoDataAmbulatorio);
					if (nodosDatas[i].getParametros().get(PolConstants.ATENDIMENTOS) != null) {
						String atdSeqs = "," + atendimento.getSeq().toString() + ((String) nodosDatas[i].getParametros().get(PolConstants.ATENDIMENTOS));
						nodosDatas[i].addParam(PolConstants.ATENDIMENTOS, atdSeqs);
					} else {
						String atdSeqs = "," + atendimento.getSeq().toString();
						nodosDatas[i].addParam(PolConstants.ATENDIMENTOS, atdSeqs);
					}
					break;
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			if (nodosDatas[i] != null && !nodosDatas[i].getNodos().isEmpty()) {
				nodoPai.addNodos(nodosDatas[i]);
			}
		}
		return especialidades;
	}

	private void montarListaEmergencia(NodoPOLVO vo, final Boolean paramAtivaDigitalizacaoPOL, final Boolean acessoDocsDigitalizadosEmePOL) throws ApplicationBusinessException {
		prontuarioOnlineFacade.processarNodosEmergenciaPol(vo, (Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE));

		if (Boolean.TRUE.equals(paramAtivaDigitalizacaoPOL) && Boolean.TRUE.equals(acessoDocsDigitalizadosEmePOL)) {
			montaNodoDocumentosDigitalizados(vo, DominioOrigemDocsDigitalizados.EME);
		}
	}

	public Date getDataUltimoDiaMes(Date data) {
		if (data != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(data);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			return c.getTime();
		}
		return null;
	}

	/**
	 * Obtem todos AghNodoPol da base.
	 * 
	 * @return Lista de AghNodoPol
	 */
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem() {
		return getAghuFacade().recuperarAghNodoPolPorOrdem();
	}

	@SuppressWarnings("unchecked")
	public List<NodoPOLVO> obterNodosExpandidos() throws ApplicationBusinessException {
		List<NodoPOLVO> nodosPOL = new LinkedList<NodoPOLVO>();
		AghParametros parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_NODO_PADRAO_POL_PATOLOGIA);
		String nodos = (String) parametro.getVlrTexto();
		if (!StringUtils.isBlank(nodos)) {
			Collection<String> ListaNodos = CollectionUtils.collect(Arrays.asList(nodos.split("\\|")), new Transformer() {
				public Object transform(final Object o) {
					return (String) o;
				}
			});
			for (String nodo : ListaNodos) {
				nodosPOL.add(new NodoPOLVO(null, null, nodo, null, false));
			}
		}
		return nodosPOL;
	}

//	public Integer obterProntuarioSelecionado() {
//		Integer prontuario = (Integer) obterContextoSessao("AIP_PRONTUARIO_PACIENTE");
//		return prontuario;
//	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public class EspecialidadePredicate implements Predicate, Serializable {

		private static final long serialVersionUID = 5633766978029907089L;

		private final AghEspecialidades iEspecialidade;

		public EspecialidadePredicate(AghEspecialidades especialidade) {
			super();
			iEspecialidade = especialidade;
		}

		public boolean evaluate(Object object) {
			if (object instanceof AghAtendimentos) {
				AghAtendimentos value = (AghAtendimentos) object;
				return value.getEspecialidadeAtendimento().equals(iEspecialidade);
			} else {
				return false;
			}
		}
	}

	// --[EXAMES]
	private void montarListaExames(NodoPOLVO polvo, Boolean permissaoADM, Boolean permissaoFluxo) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		if (permissaoFluxo) {
			NodoPOLVO noFluxo = new NodoPOLVO(polvo.getProntuario(), "fluxograma-exame", "Fluxogramas", PolConstants.IMAGES_ICONS_FLUXOGRAMA, PolConstants.PESQUISA_FLUXOGRAMA_XHTML);
			noFluxo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			noFluxo.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
			noFluxo.setAbreTab(true);
			polvo.addNodos(noFluxo);
		}

		NodoPOLVO noPaiOrdem = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_ORDEM_CRONOLOGICA.getTipo(), "Órdem Cronológica", PolConstants.IMAGES_ICONS_ORDEM_CRONOLOGICA,
				PolConstants.CONSULTAR_EXAMES_XHTML);
		noPaiOrdem.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiOrdem.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
		noPaiOrdem.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiOrdem);

		NodoPOLVO noPaiLaboratorios = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_LABORATORIOS_SERVICOS.getTipo(), "Laboratório/Serviços", PolConstants.IMAGES_ICONS_BUILDING,
				PolConstants.CONSULTAR_EXAMES_XHTML);
		noPaiLaboratorios.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiLaboratorios.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
		noPaiLaboratorios.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiLaboratorios);

		NodoPOLVO noPaiAmostras = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_AMOSTRAS_COLETADAS.getTipo(), "Amostras Coletadas", PolConstants.IMAGES_ICONS_AMOSTRAS_COLETADAS,
				PolConstants.CONSULTAR_EXAMES_XHTML);
		noPaiAmostras.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiAmostras.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
		noPaiAmostras.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiAmostras);

		if (!permissaoADM) {
			NodoPOLVO pesq = new NodoPOLVO(polvo.getProntuario(), "pesquisa-exame", "Pesquisas", PolConstants.IMAGES_ICONS_EXAMES, false);
			pesq.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			pesq.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
			pesq.setAbreTab(true);
			pesq.setPagina(PolConstants.PESQUISA_EXAMES_XHTML);
			polvo.addNodos(pesq);
		}
	}

	// --[EXAMES HIST]
	private void montarListaExamesHist(NodoPOLVO polvo, Boolean permissaoADM, Boolean permissaoFluxo) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		if (permissaoFluxo) {
			NodoPOLVO noFluxo = new NodoPOLVO(polvo.getProntuario(), "fluxograma-exame-hist", "Fluxogramas", PolConstants.IMAGES_ICONS_FLUXOGRAMA, PolConstants.PESQUISA_FLUXOGRAMA_XHTML);
			noFluxo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			noFluxo.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
			noFluxo.setAbreTab(true);
			polvo.addNodos(noFluxo);
		}

		NodoPOLVO noPaiOrdem = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_ORDEM_CRONOLOGICA_HIST.getTipo(), "Órdem Cronológica", PolConstants.IMAGES_ICONS_ORDEM_CRONOLOGICA,
				PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
		noPaiOrdem.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiOrdem.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		noPaiOrdem.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiOrdem);

		NodoPOLVO noPaiLaboratorios = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_LABORATORIOS_SERVICOS_HIST.getTipo(), "Laboratório/Serviços", PolConstants.IMAGES_ICONS_BUILDING,
				PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
		noPaiLaboratorios.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiLaboratorios.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		noPaiLaboratorios.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiLaboratorios);

		NodoPOLVO noPaiAmostras = new NodoPOLVO(polvo.getProntuario(), DominioNodoPOL.EXAME_AMOSTRAS_COLETADAS_HIST.getTipo(), "Amostras Coletadas", PolConstants.IMAGES_ICONS_AMOSTRAS_COLETADAS,
				PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
		noPaiAmostras.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		noPaiAmostras.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		noPaiAmostras.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(noPaiAmostras);

		if (!permissaoADM) {
			NodoPOLVO pesq = new NodoPOLVO(polvo.getProntuario(), "pesquisa-exame-hist", "Pesquisas", PolConstants.IMAGES_ICONS_EXAMES, false);
			pesq.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			pesq.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
			pesq.setAbreTab(true);
			pesq.setPagina(PolConstants.PESQUISA_EXAMES_HIST_XHTML);
			polvo.addNodos(pesq);
		}
	}

	/**
	 * Adiciona os itens filhos de exames > amostras coletadas
	 * 
	 * @param no
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException
	 * @since 17/02/2012
	 */
	// --[EXAMES-AMOSTRAS COLETADAS]
	private void gerarSubArvoreAmostrasColetadasItens(NodoPOLVO polvo, boolean hist) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);
		List<ExameAmostraColetadaVO> amostrasColetadas;
		String page = PolConstants.CONSULTAR_EXAMES_XHTML;

		if (hist) {
			amostrasColetadas = prontuarioOnlineFacade.pesquisarExamesAmostrasColetadasHist((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE));
			page = PolConstants.CONSULTAR_EXAMES_HIST_XHTML;
		} else {
			amostrasColetadas = prontuarioOnlineFacade.pesquisarExamesAmostrasColetadas((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		}

		Map<Integer, NodoPOLVO> grupos = new HashMap<>();
		List<String> grupoDatasCache = new ArrayList<>();
		NodoPOLVO noTipo = null;

		int idx = 0;
		for (ExameAmostraColetadaVO vo : amostrasColetadas) {

			NodoPOLVO noAmostra = new NodoPOLVO(polvo.getProntuario(), "Amostra Exames", DateFormatUtil.fomataDiaMesAno(vo.getDtExame()), PolConstants.IMAGES_ICONS_CLOCK, page);
			noAmostra.addParam(DT_EXAME, vo.getDtExame());
			noAmostra.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
			noAmostra.addParam("gmaSeq", vo.getSeqGrupoMaterialAnalise());
			noAmostra.setOrdem(idx);

			if (grupos.containsKey(vo.getSeqGrupoMaterialAnalise())) {
				noTipo = grupos.get(vo.getSeqGrupoMaterialAnalise());
			} else {
				noTipo = new NodoPOLVO(polvo.getProntuario(), "Grupo Material Analise", vo.getDescGrpMatAnalise(), PolConstants.IMAGES_ICON_AMOSTRAS_COLETADAS, page);
				noTipo.addParam("gmaSeq", vo.getSeqGrupoMaterialAnalise());
				noTipo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
				noTipo.setOrdem(idx);
				grupos.put(vo.getSeqGrupoMaterialAnalise(), noTipo);
				polvo.addNodos(noTipo);
				grupoDatasCache.clear();
			}
			String grupoDatasCacheKey = vo.getSeqGrupoMaterialAnalise() + vo.getDtExame().toString().substring(0, 10);
			if (!grupoDatasCache.contains(grupoDatasCacheKey)) {
				noTipo.addNodos(noAmostra);
				grupoDatasCache.add(grupoDatasCacheKey);
			}
			idx++;
		}
	}

	private NodoPOLVO getNenhumRegistro(Integer prontuario) {
		return new NodoPOLVO(prontuario, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO, PolConstants.NENHUM_REGISTRO_ICONE, true);
	}

	/**
	 * Adiciona os itens filhos de exames > ordem cronologica
	 * 
	 * @param no
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException
	 * @since 15/02/2012
	 */
	// --[EXAMES-ORDEM CRONOLOGICA]
	private void gerarSubArvoreOrdemCronologicaItens(NodoPOLVO polvo, boolean hist) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);
		List<ExameOrdemCronologicaVO> ordemCronologica = null;
		String page = PolConstants.CONSULTAR_EXAMES_XHTML;
		if (hist) {
			ordemCronologica = prontuarioOnlineFacade.pesquisarExamesOrdemCronologicaHist((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE));
			page = PolConstants.CONSULTAR_EXAMES_HIST_XHTML;
		} else {
			ordemCronologica = prontuarioOnlineFacade.pesquisarExamesOrdemCronologica((Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		}

		int idx = 0;
		Date data = null;
		for (ExameOrdemCronologicaVO vo : ordemCronologica) {
			if (!compareDateString(vo.getDtExame(), data)) {
				NodoPOLVO noExameOrdem = new NodoPOLVO(polvo.getProntuario(), "Exame-ordemCronologica-data", DateFormatUtil.fomataDiaMesAno(vo.getDtExame()), PolConstants.IMAGES_ICONS_CLOCK,
						Integer.valueOf(idx++), page);

				noExameOrdem.addParam(DT_EXAME, vo.getDtExame());
				noExameOrdem.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
				polvo.addNodos(noExameOrdem);
			}
			data = vo.getDtExame();
		}
	}

	private boolean compareDateString(Date d1, Date d2) {
		if (d2 == null) {
			return false;

		}
		return d1.toString().substring(0, 10).equals(d2.toString().substring(0, 10));
	}

	// --[EXAMES-LABORATORIOS E SERVICOS]
	private void gerarSubArvoreLaboratorioItens(NodoPOLVO polvo, boolean hist) {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);
		NodoPOLVO nodoUnidade = null;
		NodoPOLVO nodoData = null;
		// NodoPOLVO nodoExame = null;
		VAelPesquisaPolExameUnidade unidadeAnterior = null;
		VAelPesquisaPolExameUnidadeHist unidadeAnteriorHist = null;
		Short unfSeqAnt = 0;
		Date dthrAnt = null;

		if (hist) {
			List<VAelPesquisaPolExameUnidadeHist> arvoreUnidades = prontuarioOnlineFacade.buscarArvoreLaboratorioServicosDeExamesHist(codPaciente);
			for (VAelPesquisaPolExameUnidadeHist unidade : arvoreUnidades) {

				if (!unfSeqAnt.equals(unidade.getUnfSeq())) {
					nodoUnidade = new NodoPOLVO(polvo.getProntuario(), "laboratorios_servicos_unidades", unidade.getUnfDescricao(), PolConstants.IMAGES_ICONS_BUILDING,
							PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
					nodoUnidade.addParam(UNF_SEQ, unidade.getUnfSeq());
					nodoUnidade.setQuebraLinha(true);
					nodoUnidade.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
					polvo.addNodos(nodoUnidade);
					unfSeqAnt = unidade.getUnfSeq();
				}

				if (unidade.getData() != null
						&& (dthrAnt == null || (dthrAnt != null && (!DateUtil.isDatasIguais(DateUtil.truncaData(dthrAnt), DateUtil.truncaData(unidade.getData())) || (DateUtil.isDatasIguais(
								DateUtil.truncaData(dthrAnt), DateUtil.truncaData(unidade.getData())) && !nodoUnidade.getDescricao().equals(unidadeAnteriorHist.getUnfDescricao())))))) {

					SimpleDateFormat df = new SimpleDateFormat(PolConstants.MASK_DATA);
					nodoData = new NodoPOLVO(polvo.getProntuario(), "laboratorios_servicos_unidades_data", df.format(unidade.getData()), PolConstants.IMAGES_ICONS_TIME,
							PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
					nodoData.addParam(UNF_SEQ, unidade.getUnfSeq());
					nodoData.addParam(DT_EXAME, unidade.getData());
					nodoData.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
					nodoUnidade.addNodos(nodoData);
					dthrAnt = unidade.getData();

				}
				unidadeAnteriorHist = unidade;
			}

		} else {
			List<VAelPesquisaPolExameUnidade> arvoreUnidades = prontuarioOnlineFacade.buscaArvoreLaboratorioServicosDeExames(codPaciente);

			for (VAelPesquisaPolExameUnidade unidade : arvoreUnidades) {

				if (!unfSeqAnt.equals(unidade.getUnfSeq())) {
					nodoUnidade = new NodoPOLVO(polvo.getProntuario(), "laboratorios_servicos_unidades", unidade.getUnfDescricao(), PolConstants.IMAGES_ICONS_BUILDING,
							PolConstants.CONSULTAR_EXAMES_XHTML);
					nodoUnidade.addParam(UNF_SEQ, unidade.getUnfSeq());
					nodoUnidade.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
					nodoUnidade.setQuebraLinha(true);
					polvo.addNodos(nodoUnidade);
					unfSeqAnt = unidade.getUnfSeq();
				}

				if (unidade.getData() != null
						&& (dthrAnt == null || (dthrAnt != null && (!DateUtil.isDatasIguais(DateUtil.truncaData(dthrAnt), DateUtil.truncaData(unidade.getData())) || (DateUtil.isDatasIguais(
								DateUtil.truncaData(dthrAnt), DateUtil.truncaData(unidade.getData())) && !nodoUnidade.getDescricao().equals(unidadeAnterior.getUnfDescricao())))))) {

					SimpleDateFormat df = new SimpleDateFormat(PolConstants.MASK_DATA);
					nodoData = new NodoPOLVO(polvo.getProntuario(), "laboratorios_servicos_unidades_data", df.format(unidade.getData()), PolConstants.IMAGES_ICONS_TIME,
							PolConstants.CONSULTAR_EXAMES_XHTML);
					nodoData.addParam(UNF_SEQ, unidade.getUnfSeq());
					nodoData.addParam(DT_EXAME, unidade.getData());
					nodoData.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
					nodoUnidade.addNodos(nodoData);
					dthrAnt = unidade.getData();

				}
				unidadeAnterior = unidade;
			}
		}
	}

	public void montarListaMedicamentos(NodoPOLVO vo, boolean acessoAdminPOL) {
		Integer codPaciente = (Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		NodoPOLVO nodoAntibiotico = new NodoPOLVO(vo.getProntuario(), PolConstants.ANTIBIOTICOS, "Antibióticos", PolConstants.IMAGES_ICONS_MED_ANTIBIOTICOS, PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
		NodoPOLVO nodoQuimioterapico = new NodoPOLVO(vo.getProntuario(), PolConstants.QUIMIOTERAPICOS, "Quimioterápicos", PolConstants.IMAGES_ICONS_MED_TUBERCULOSTATICOS,
				PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
		NodoPOLVO nodoTuberculostatico = new NodoPOLVO(vo.getProntuario(), PolConstants.TUBERCULOSTATICOS, "Tuberculostáticos", PolConstants.IMAGES_ICONS_MED_QUIMIOTERAPICOS,
				PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);

		nodoAntibiotico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoAntibiotico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
		nodoQuimioterapico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoQuimioterapico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
		nodoTuberculostatico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoTuberculostatico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);

		// -------------- NODO ANTIBIÓTICOS
		if (!this.prontuarioOnlineFacade.verificarExisteMedicamento(codPaciente, PolConstants.ANTIBIOTICOS)) {
			nodoAntibiotico.addNodos(getNenhumRegistro(vo.getProntuario()));
			nodoAntibiotico.setPagina(null);
		} else {
			addNodosDataMedicamento(nodoAntibiotico, codPaciente, PolConstants.ANTIBIOTICOS);
		}

		// -------------- NODO QUIMIOTERÁPICOS
		if (!acessoAdminPOL) {
			if (!this.prontuarioOnlineFacade.verificarExisteMedicamento(codPaciente, PolConstants.QUIMIOTERAPICOS)) {
				nodoQuimioterapico.addNodos(getNenhumRegistro(vo.getProntuario()));
				nodoQuimioterapico.setPagina(null);
			} else {
				addNodosDataMedicamento(nodoQuimioterapico, codPaciente, PolConstants.QUIMIOTERAPICOS);
			}
		}

		// -------------- NODO TUBERCULOSTÁTICOS
		if (!this.prontuarioOnlineFacade.verificarExisteMedicamento(codPaciente, PolConstants.TUBERCULOSTATICOS)) {
			nodoTuberculostatico.addNodos(getNenhumRegistro(vo.getProntuario()));
			nodoTuberculostatico.setPagina(null);
		} else {
			addNodosDataMedicamento(nodoTuberculostatico, codPaciente, PolConstants.TUBERCULOSTATICOS);
		}

		vo.addNodos(nodoAntibiotico);
		if (!acessoAdminPOL) {
			vo.addNodos(nodoQuimioterapico);
		}
		vo.addNodos(nodoTuberculostatico);
	}

	private void addNodosDataMedicamento(NodoPOLVO vo, Integer codPaciente, String tipo) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Date> lista = this.prontuarioOnlineFacade.obterDataMedicamentos(codPaciente, tipo, null);

		Date d = null;
		for (Date data : lista) {
			if(d == null || !d.equals(DateUtil.truncaData(data))){
				d = DateUtil.truncaData(data);
				NodoPOLVO no = new NodoPOLVO(vo.getProntuario(), tipo, df.format(data), PolConstants.IMAGES_ICONS_TIME, PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
				no.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
				no.addParam(NodoPOLVO.IS_HISTORICO, Boolean.FALSE);
				no.addParam("data", data);
				vo.addNodos(no);
			}
		}
	}

	public void montarListaMedicamentosHist(NodoPOLVO vo, boolean acessoAdminPOL) {
		Integer codPaciente = (Integer) vo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		NodoPOLVO nodoAntibiotico = new NodoPOLVO(vo.getProntuario(), PolConstants.ANTIBIOTICOS, "Antibióticos", PolConstants.IMAGES_ICONS_MED_ANTIBIOTICOS, PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
		NodoPOLVO nodoQuimioterapico = new NodoPOLVO(vo.getProntuario(), PolConstants.QUIMIOTERAPICOS, "Quimioterápicos", PolConstants.IMAGES_ICONS_MED_TUBERCULOSTATICOS,
				PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
		NodoPOLVO nodoTuberculostatico = new NodoPOLVO(vo.getProntuario(), PolConstants.TUBERCULOSTATICOS, "Tuberculostáticos", PolConstants.IMAGES_ICONS_MED_QUIMIOTERAPICOS,
				PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);

		nodoAntibiotico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoAntibiotico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		nodoQuimioterapico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoQuimioterapico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		nodoTuberculostatico.addParam(NodoPOLVO.COD_PACIENTE, vo.getParametros().get(NodoPOLVO.COD_PACIENTE));
		nodoTuberculostatico.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);

		// -------------- NODO ANTIBIÓTICOS
		if (this.prontuarioOnlineFacade.obterMedicamentosHistCount(codPaciente, PolConstants.ANTIBIOTICOS).equals(0L)) {
			nodoAntibiotico.addNodos(getNenhumRegistro(vo.getProntuario()));
			nodoAntibiotico.setPagina(null);
		} else {
			addNodosDataMedicamentoHist(nodoAntibiotico, codPaciente, PolConstants.ANTIBIOTICOS);
		}

		// -------------- NODO QUIMIOTERÁPICOS
		if (acessoAdminPOL) {
			if (!this.prontuarioOnlineFacade.obterMedicamentosHistCount(codPaciente, PolConstants.QUIMIOTERAPICOS).equals(0L)) {
				nodoQuimioterapico.addNodos(getNenhumRegistro(vo.getProntuario()));
				nodoQuimioterapico.setPagina(null);
			} else {
				addNodosDataMedicamentoHist(nodoQuimioterapico, codPaciente, PolConstants.QUIMIOTERAPICOS);
			}
		}

		// -------------- NODO TUBERCULOSTÁTICOS
		if (this.prontuarioOnlineFacade.obterMedicamentosHistCount(codPaciente, PolConstants.TUBERCULOSTATICOS).equals(0L)) {
			nodoTuberculostatico.addNodos(getNenhumRegistro(vo.getProntuario()));
			nodoTuberculostatico.setPagina(null);
		} else {
			addNodosDataMedicamentoHist(nodoTuberculostatico, codPaciente, PolConstants.TUBERCULOSTATICOS);
		}

		vo.addNodos(nodoAntibiotico);
		if (!acessoAdminPOL) {
			vo.addNodos(nodoQuimioterapico);
		}
		vo.addNodos(nodoTuberculostatico);
	}

	private void addNodosDataMedicamentoHist(NodoPOLVO vo, Integer codPaciente, String tipo) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<VAipPolMdtosAghuHist> lista = this.prontuarioOnlineFacade.obterMedicamentosHist(codPaciente, tipo, null);
		Date dt = null;
		for (VAipPolMdtosAghuHist mdtos : lista) {
			if (!mdtos.getDataInicio().equals(dt)) {
				NodoPOLVO no = new NodoPOLVO(vo.getProntuario(), tipo, df.format(mdtos.getDataInicio()), PolConstants.IMAGES_ICONS_TIME, PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
				no.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
				no.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
				no.addParam("data", mdtos.getDataInicio());
				vo.addNodos(no);
				dt = mdtos.getDataInicio();
			}
		}
	}

	private void montarListaSessaoTerapeutica(NodoPOLVO polvo) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		// --[FISIOTERAPIA]
		NodoPOLVO nodoFisioterapia = new NodoPOLVO(polvo.getProntuario(), "sessoesFisioterapia", "Fisioterapia", PolConstants.IMAGES_ICONS_FISIOTERAPIA, 1, null);
		nodoFisioterapia.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

		List<NodoPOLVO> listFisio = prontuarioOnlineFacade.carregaNosFisioterapia(codPaciente, "folhaTratamentoFisioterapia", PolConstants.LISTAR_SESSOES_FISIOTERAPICAS_XHTML,
				PolConstants.IMAGES_ICONS_TIME);
		if (listFisio.isEmpty()) {
			nodoFisioterapia.addNodos(getNenhumRegistro(polvo.getProntuario()));
		} else {
			nodoFisioterapia.addNodos(listFisio);
		}
		polvo.addNodos(nodoFisioterapia);

		// --[QUIMIOTERAPIA]
		NodoPOLVO nodoQuimioterapia = new NodoPOLVO(polvo.getProntuario(), "sessoesQuimioterapia", "Quimioterapia", PolConstants.IMAGES_ICONS_QUIMIOTERAPIA, 2, null);
		nodoQuimioterapia.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		
		List<NodoPOLVO> listQuimio = prontuarioOnlineFacade.carregaNosQuimioterapia(codPaciente, "folhaTratamentoFisioterapia", PolConstants.LISTAR_SESSOES_QUIMIOTERAPICAS_XHTML,
				PolConstants.IMAGES_ICONS_TIME);
		if (listQuimio.isEmpty()) {
			nodoQuimioterapia.addNodos(getNenhumRegistro(polvo.getProntuario()));
		} else {
			nodoQuimioterapia.addNodos(listQuimio);
		}
		polvo.addNodos(nodoQuimioterapia);

	}

	private void montarListaDadosHistorico(NodoPOLVO polvo) throws ApplicationBusinessException {
		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		DominioNodoPOL exame = DominioNodoPOL.EXAME_HIST;
		NodoPOLVO nodo = new NodoPOLVO(polvo.getProntuario(), exame.getTipo(), "Exames", PolConstants.IMAGES_ICONS_EXAMES, PolConstants.CONSULTAR_EXAMES_HIST_XHTML);
		nodo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		nodo.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		nodo.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(nodo);

		DominioNodoPOL medicamento = DominioNodoPOL.MEDICAMENTOS_HIST;
		nodo = new NodoPOLVO(polvo.getProntuario(), medicamento.getTipo(), "Medicamentos", PolConstants.IMAGES_ICONS_MEDICAMENTOS, PolConstants.CONSULTAR_MEDICAMENTOS_XHTML);
		nodo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		nodo.addParam(NodoPOLVO.IS_HISTORICO, Boolean.TRUE);
		nodo.addNodos(getNenhumRegistro(polvo.getProntuario()));
		polvo.addNodos(nodo);
	}

	private void montaNodoDocumentosDigitalizadosAdministrativos(NodoPOLVO polvo, DominioOrigemDocsDigitalizados origem) throws ApplicationBusinessException {

		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		NodoPOLVO nodoDigitalizacao = new NodoPOLVO( polvo.getProntuario(), DominioNodoPOL.DIGITALIZACAO.getTipo(), PROCESSOS_ADMINISTRATIVOS,
													 PolConstants.IMAGES_ICONS_PROCESSOS, PolConstants.CONSULTAR_DOCS_DIGITALIZACAO_XHTML);
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		ParametrosGEDVO parametros = new ParametrosGEDAdministrativosVO(codPaciente, polvo.getProntuario().toString(), null, 
																		(DominioOrigemDocsDigitalizados.INT.equals(origem) ? "Internação" : "Ambulatorio"), 
																		null, null, null, servidorLogado.getUsuario());
		
		List<DocumentoGEDVO> docs = digitalizacaoPOLFacade.buscaUrlsDocumentosGEDAdminstrativos(parametros);
		
		if(docs != null && !docs.isEmpty()){
			setarParametrosNodoDigitalizacao(nodoDigitalizacao, origem, codPaciente, DominioFichasDocsDigitalizados.ADMINISTRATIVOS); 
			nodoDigitalizacao.setNomePaciente(polvo.getNomePaciente());
			nodoDigitalizacao.setDescricao(nodoDigitalizacao.getDescricao() + " (" + docs.size() + ")");
			polvo.getNodos().add(nodoDigitalizacao);
		}
	}

	private void montaNodoDocumentosDigitalizados(NodoPOLVO polvo, DominioOrigemDocsDigitalizados origem) throws ApplicationBusinessException {

		Integer codPaciente = (Integer) polvo.getParametros().get(NodoPOLVO.COD_PACIENTE);

		NodoPOLVO nodoDigitalizacao = new NodoPOLVO( polvo.getProntuario(), DominioNodoPOL.DIGITALIZACAO.getTipo(), DOCUMENTOS_DIGITALIZADOS,
													 PolConstants.IMAGES_ICONS_CD, PolConstants.CONSULTAR_DOCS_DIGITALIZACAO_XHTML);
		nodoDigitalizacao.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ParametrosGEDVO parametros = new ParametrosGEDAtivosVO(codPaciente, polvo.getProntuario().toString(), null, origem.getDescricao(), null, null, null, servidorLogado.getUsuario());

		List<DocumentoGEDVO> docs = digitalizacaoPOLFacade.buscaUrlsDocumentosGEDAtivos(parametros);
		if(docs != null && !docs.isEmpty()){
			setarParametrosNodoDigitalizacao(nodoDigitalizacao, origem, codPaciente, DominioFichasDocsDigitalizados.ATIVOS);
			nodoDigitalizacao.setNomePaciente(polvo.getNomePaciente());
		nodoDigitalizacao.setDescricao(nodoDigitalizacao.getDescricao() + " (" + docs.size() + ")");
		polvo.getNodos().add(nodoDigitalizacao);
	}
	}

	private void setarParametrosNodoDigitalizacao(NodoPOLVO nodo, DominioOrigemDocsDigitalizados origem, Integer codPaciente, DominioFichasDocsDigitalizados ficha) {
		if(origem != null){
			nodo.addParam(NodoPOLVO.ORIGEM_DIGITALIZACAO, origem.name());
	}

		nodo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);
		nodo.addParam(NodoPOLVO.FICHA, ficha.name());
	}

	private NodoPOLVO  processarNodoInformacoesPerinatais(Integer codPaciente,Integer prontuario) {
		
		//NodoPOLVO voGest = new NodoPOLVO(codPaciente,nodo.getNome(),nodo.getDescricao(), "informacoesPerinatais", PolConstants.INFORMACOES_PERINATAIS_LIST_POL_XHTML, icone);
	    NodoPOLVO polvo = new NodoPOLVO();
	    polvo.setProntuario(prontuario);
		polvo.setDescricao("Informacoes Perinatais");
		polvo.setIcone(PolConstants.IMAGES_ICONS_CONSTRUCTION);
		polvo.setPagina(PolConstants.INFORMACOES_PERINATAIS_LIST_POL_XHTML);
		polvo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente); // ATENÇÃO: NÃO HÁ OUTROS PARÂMETROS
		
		return polvo;
	}


    private NodoPOLVO  processarNodoHistoricaObstetrica(Integer codPaciente,Integer prontuario) {		
			
	    NodoPOLVO polvo = new NodoPOLVO();
	    polvo.setProntuario(prontuario);
		polvo.setDescricao("História Obstetrica");
		polvo.setIcone(PolConstants.IMAGES_ICONS_FOLDER_PAGE);
		polvo.setPagina(null);
		polvo.addParam(NodoPOLVO.COD_PACIENTE, codPaciente); // ATENÇÃO: NÃO HÁ OUTROS PARÂMETROS		
		
		List<McoGestacoes> gestacoes = perinatologiaFacade.pesquisarMcoGestacoes(codPaciente);  

		for (McoGestacoes mcoGestacoes : gestacoes) {
			Short gsoSeqp = mcoGestacoes.getId().getSeqp();

		    NodoPOLVO nodoGestacao = new NodoPOLVO();
		    nodoGestacao.setProntuario(prontuario);
		    nodoGestacao.setDescricao("Gestação " + gsoSeqp);
		    nodoGestacao.setIcone(PolConstants.IMAGES_ICONS_GESTACAO);
		    nodoGestacao.setPagina(PolConstants.HISTORIA_OBSTETRICA_POR_GESTACAO_PACIENTE_LIST_POL_XHTML);
		    nodoGestacao.setProntuario(prontuario);
		    nodoGestacao.addParam(NodoPOLVO.GSO_SEQP, gsoSeqp); 
		    nodoGestacao.addParam(NodoPOLVO.COD_PACIENTE, codPaciente);

		    polvo.addNodos(nodoGestacao);
		}
	
		return polvo;
	}

}