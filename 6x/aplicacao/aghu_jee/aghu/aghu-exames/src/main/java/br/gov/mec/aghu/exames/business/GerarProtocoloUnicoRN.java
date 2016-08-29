package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioLwsSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioLwsTipoAmostra;
import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.dominio.DominioLwsTipoStatusTransacao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.LwsComAmostraDAO;
import br.gov.mec.aghu.exames.dao.LwsComPacienteDAO;
import br.gov.mec.aghu.exames.dao.LwsComSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.LwsComunicacaoDAO;
import br.gov.mec.aghu.exames.solicitacao.business.EtiquetasON;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.LwsComAmostra;
import br.gov.mec.aghu.model.LwsComPaciente;
import br.gov.mec.aghu.model.LwsComSolicitacaoExame;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * ORADB PROCEDURE AELP_GERA_PROT_UNIC
 * @author aghu
 *
 */
@Stateless
public class GerarProtocoloUnicoRN extends BaseBusiness{


private static final String AMB = "AMB";

@EJB
private LwsComunicacaoRN lwsComunicacaoRN;

@EJB
private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;

private static final Log LOG = LogFactory.getLog(GerarProtocoloUnicoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private LwsComAmostraDAO lwsComAmostraDAO;

@Inject
private LwsComSolicitacaoExameDAO lwsComSolicitacaoExameDAO;

@Inject
private LwsComunicacaoDAO lwsComunicacaoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private EtiquetasON etiquetasON;

@Inject
private LwsComPacienteDAO lwsComPacienteDAO;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	

	public enum GerarProtocoloUnicoRNExceptionCode implements BusinessExceptionCode {
		ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8339927483617996285L;

	/**
	 * ORADB PROCEDURE AELP_GERA_PROT_UNIC
	 * @param amostra
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	public void gerarProtocoloUnico(AelAmostras amostra, boolean cancelaInterfaceamento, String siglaExame) throws BaseException{
		
		final AelSolicitacaoExames solicitacaoExame = amostra.getSolicitacaoExame();
		final Integer soeSeq = solicitacaoExame.getSeq();
		final Short seqp = amostra.getId().getSeqp();
		
		
		/*
		 * INSERE_EM_LWS_COMUNICACAO
		 */
		
		// Chamada da FUNCTION LWSC_GET_LWS_LWF_SQ1_NEXTVAL
		final Integer valorIdComunicacao = this.getLwsComunicacaoDAO().obterProximoIdComunicacao();
		
		LwsComunicacao lwsComunicacao = new LwsComunicacao();
		
		lwsComunicacao.setIdOrigem(this.obterIdModuloLisHis());
		lwsComunicacao.setIdDestino(this.obterIdModuloGestaoAmostra());
		if(cancelaInterfaceamento){
			lwsComunicacao.setTipoComunicacao(DominioLwsTipoComunicacao.CANCELAMENTO_PEDIDO_EXAME); // 3
		}else{
			lwsComunicacao.setTipoComunicacao(DominioLwsTipoComunicacao.PEDIDO_CARGA_EXAMES); // 1
		}
		lwsComunicacao.setSeqComunicacao(valorIdComunicacao);
		lwsComunicacao.setDataHora(new Date());
		lwsComunicacao.setStatus(DominioLwsTipoStatusTransacao.NAO_PROCESSADA); // 0
		
		this.getLwsComunicacaoRN().inserir(lwsComunicacao);
		
		
		/*
		 * INSERE_EM_LWS_COM_PACIENTES
		 */

		LwsComPaciente lwsComPaciente = new LwsComPaciente();
		
		lwsComPaciente.setLwsComunicacao(lwsComunicacao);
		
		// Chamada para FUNCTION AELC_LAUDO_COD_PAC
		Integer codigoPacienteLis = this.getRelatorioTicketExamesPacienteON().buscarLaudoCodigoPaciente(solicitacaoExame);
		lwsComPaciente.setCodigoPacienteLis(this.converterValorStringLws(codigoPacienteLis)); 
		
		// Chamada para FUNCTION AELC_LAUDO_PRNT_PAC
		String prontuario = this.getRelatorioTicketExamesPacienteON().buscarLaudoProntuarioPaciente(solicitacaoExame);
		if(prontuario != null && "0".equalsIgnoreCase(prontuario)){
			// Ajuste necessário para não impactar na migração de AELC_LAUDO_PRNT_PAC
			prontuario = null;
		}
		lwsComPaciente.setProntuario(prontuario); 
		
		// Chamada para FUNCTION AELC_LAUDO_NOME_PAC
		String nome = this.getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(solicitacaoExame);
		lwsComPaciente.setNome(nome);
		
		// Chamada para FUNCTION AELC_LAUDO_SEXO_PAC
		String sexo = this.obterLaudoSexoPaciente(solicitacaoExame);
		lwsComPaciente.setSexo(sexo);
		
		// Chamada para FUNCTION AELC_LAUDO_DTNS_PAC
		Date dataNascimento = this.getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(solicitacaoExame); 
		lwsComPaciente.setDataNascimento(dataNascimento);

		// Insere LwsComPaciente
		this.getLwsComPacienteDAO().persistir(lwsComPaciente);
		this.getLwsComPacienteDAO().flush();

		
		/*
		 * INSERE_EM_LWS_COM_AMOSTRAS
		 */  

		LwsComAmostra lwsComAmostra = new LwsComAmostra();
		
		lwsComAmostra.setLwsComunicacao(lwsComunicacao);
		lwsComAmostra.setLwsComPaciente(lwsComPaciente);
		
		final String codigoAmostra = this.getNumeroSolicitacaoExameFormatado(soeSeq, seqp);
		lwsComAmostra.setCodigoAmostra(codigoAmostra);	

		// Obtém o valor do Material de Análise
		Integer valorAmostraMaterialLis = amostra.getMateriaisAnalises() != null ? amostra.getMateriaisAnalises().getSeq() : null;
		lwsComAmostra.setMaterialLis(this.converterValorStringLws(valorAmostraMaterialLis)); // Gravar código do material conforme o AGH

		lwsComAmostra.setTipoAmostraLis(DominioLwsTipoAmostra.N);
		lwsComAmostra.setSolicitacao(soeSeq.toString());

		// Obtém o valor do Número Único
		Integer valorAmostraNroInterno = null;
		
		if(amostra.getNroUnico() != null){
			valorAmostraNroInterno = amostra.getNroUnico();
		} else if(amostra.getNroInterno() != null){
			valorAmostraNroInterno = amostra.getNroInterno().intValue();
		}
		
		lwsComAmostra.setNroInterno(this.converterValorStringLws(valorAmostraNroInterno));

		lwsComAmostra.setOrigem(this.obterOrigemIg(solicitacaoExame)); // v_origem
		lwsComAmostra.setPontoCurva(0);
		lwsComAmostra.setInfoClinicas(solicitacaoExame.getInformacoesClinicas()); // v_informacoes_clinicas
		String sala = "";
		if (solicitacaoExame.getAtendimento() != null){
			sala = etiquetasON.localizarPaciente(solicitacaoExame.getAtendimento());
			if (sala.length() > 19){
				sala = sala.substring(0, 19);
			} 
		}
		
		lwsComAmostra.setSala(sala);
		
		StringBuffer local = new StringBuffer(50);
		if (solicitacaoExame.getUnidadeFuncional() != null){
			local.append(solicitacaoExame.getUnidadeFuncional().getSeq())
			.append('-')
			.append(solicitacaoExame.getUnidadeFuncional().getDescricao());
			if (local.length() > 20){
				local.setLength(20);
			} 
		}
		lwsComAmostra.setLocal(local.toString());

		// Insere LwsComAmostra
		this.getLwsComAmostraDAO().persistir(lwsComAmostra);
		this.getLwsComAmostraDAO().flush();


		/*
		 * INSERE_EM_LWS_COM_SOLICITACAO_EXAMES
		 */
		
		// Pesquisa Amostra Item Exame
		List<AelAmostraItemExames> listaAmostraItemExames = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamesGerarProtocoloUnicoLwsComSolicitacaoExame(
				soeSeq, seqp.intValue(), siglaExame);
		
		if(listaAmostraItemExames != null && !listaAmostraItemExames.isEmpty()){
			
			for (AelAmostraItemExames amostraItemExames : listaAmostraItemExames) {
				
				AelItemSolicitacaoExames itemSolicitacaoExame = amostraItemExames.getAelItemSolicitacaoExames();
				
				LwsComSolicitacaoExame lwsComSolicitacaoExame = new LwsComSolicitacaoExame();

				lwsComSolicitacaoExame.setLwsComunicacao(lwsComunicacao);
				lwsComSolicitacaoExame.setLwsComAmostra(lwsComAmostra);
				
				lwsComSolicitacaoExame.setExameLis(this.getExameLisFormatado(itemSolicitacaoExame));
				
				lwsComSolicitacaoExame.setSituacao(DominioLwsSituacaoAmostra.N);
				
				lwsComSolicitacaoExame.setPrioridade(itemSolicitacaoExame.getTipoColeta());

				lwsComSolicitacaoExame.setDataHoraSolicitacao(itemSolicitacaoExame.getDthrProgramada());
				
				lwsComSolicitacaoExame.setProfissionalSolicitante(this.getProfissionalSolicitanteFormatado(itemSolicitacaoExame));
				
				lwsComSolicitacaoExame.setListaContagem(false);
				lwsComSolicitacaoExame.setAutoValidado(false);
				lwsComSolicitacaoExame.setVersaoLis(0);
				/*
				Integer iseSoeSeq = itemSolicitacaoExame.getItemSolicitacaoExame().getId().getSoeSeq();
				Short iseSeqp = itemSolicitacaoExame.getItemSolicitacaoExame().getId().getSeqp();
				*/
				Integer iseSoeSeq = itemSolicitacaoExame.getId().getSoeSeq();
				Short iseSeqp = itemSolicitacaoExame.getId().getSeqp();
				lwsComSolicitacaoExame.setSolicitacao(this.getNumeroSolicitacaoExameFormatado(iseSoeSeq,iseSeqp));

				// Insere LwsComSolicitacaoExame
				this.getLwsComSolicitacaoExameDAO().persistir(lwsComSolicitacaoExame);
				this.getLwsComSolicitacaoExameDAO().flush();

			}
			
		}

	}
	
	/*
	 * Métodos utilitários
	 */


	/**
	 * Obtém o id da origem da comunicação LWS
	 * Obs. O valor padrão é 90
	 */
	public Short obterIdModuloLisHis() throws ApplicationBusinessException{
		return this.obterIdSistemasModulosLws(AghuParametrosEnum.P_LIS_HIS);
	}
	
	/**
	 * Obtém o destino da comunicação LWS
	 * Obs. O valor padrão é 99
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Short obterIdModuloGestaoAmostra() throws ApplicationBusinessException{
		return this.obterIdSistemasModulosLws(AghuParametrosEnum.P_MODULO_GESTAO_AMOSTRA_VFR);
	}
	
	/**
	 * Método reutilizado para obtenção tanto do id de origem quanto destino da comunicação LWS
	 * @param parametrosEnum
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short obterIdSistemasModulosLws(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException{
		
		/* 
		 * TODO Tentar resolver o seguinte problema:  Nao foi possivel obter usuario logado atraves de ICascaFacade 
		 * Login do servidor não informado ou inexistente.
		 */
		//AghParametros parametro = this.getParametroFacade().buscarAghParametro(parametrosEnum);
		//return parametro.getVlrNumerico().shortValue();
		
		if(AghuParametrosEnum.P_MODULO_GESTAO_AMOSTRA_VFR.equals(parametrosEnum)){
			return (short)99;
		} else if (AghuParametrosEnum.P_LIS_HIS.equals(parametrosEnum)){
			return (short) 90;
		} else{
			throw new ApplicationBusinessException(GerarProtocoloUnicoRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
		}
		
	}
	
	/**
	 * Converte uma valor para o formato LWS
	 */
	private String converterValorStringLws(Object o){
		return o != null ? o.toString() : null;
	}

	/**
	 * Obtém o numero da Solicitação de Exame formatado considerando os zeros à esquerda
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	private String getNumeroSolicitacaoExameFormatado(Integer soeSeq, Short seqp){
		 return StringUtil.adicionaZerosAEsquerda(soeSeq, 8) + StringUtil.adicionaZerosAEsquerda(seqp, 3);
	}
	
	/**
	 * Obtém o Exame no formato LWS
	 * @param itemSolicitacaoExame
	 * @return
	 */
	private String getExameLisFormatado(AelItemSolicitacaoExames itemSolicitacaoExame){
		
		String sigla = itemSolicitacaoExame.getExame().getSigla();
		Integer material = itemSolicitacaoExame.getMaterialAnalise().getSeq();
		Short unidade = itemSolicitacaoExame.getUnidadeFuncional().getSeq();
		
		String parte1 = StringUtils.leftPad(sigla, 5, ' ');
		String parte2 = StringUtil.adicionaZerosAEsquerda(material, 5) + StringUtil.adicionaZerosAEsquerda(unidade, 4);

		return parte1 + parte2;
	}
	
	/**
	 * Obtém Profissional Solicitante e Responsável no formato LWS
	 * @param itemSolicitacaoExame
	 * @return
	 */
	private String getProfissionalSolicitanteFormatado(AelItemSolicitacaoExames itemSolicitacaoExame){

		RapServidores responsavel = itemSolicitacaoExame.getSolicitacaoExame().getServidorResponsabilidade();
		if(responsavel != null){
			String parte1 = StringUtil.adicionaZerosAEsquerda(responsavel.getId().getVinCodigo(), 3);
			String parte2 = StringUtil.adicionaZerosAEsquerda(responsavel.getId().getMatricula(), 7);
			return parte1 + parte2;
		}
		return null;
	}

	
	/*
	 * FUNCTIONS migradas
	 */

	/**
	 * ORADB FUNCTION AELC_LAUDO_SEXO_PAC
	 * @param soeSeq
	 * @return
	 */
	public String obterLaudoSexoPaciente(AelSolicitacaoExames solicitacaoExame){
		
		DominioSexo sexo = null;
		
		if(solicitacaoExame != null){

			AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
			
			if(atendimento != null && atendimento.getSeq() > 0){
				sexo = atendimento.getPaciente().getSexo();
			} else{
				
				AelAtendimentoDiversos atendimentoDiversos = solicitacaoExame.getAtendimentoDiverso();
				
				if(atendimentoDiversos != null){
				
					if(atendimentoDiversos.getAbsCandidatosDoadores() != null){
						sexo = DominioSexo.getInstance(atendimentoDiversos.getAbsCandidatosDoadores().getSexo()); 
					} else if (atendimentoDiversos.getAipPaciente() != null){
						sexo = atendimentoDiversos.getAipPaciente().getSexo();
					}

				}
			}
			
		}

		return sexo != null ? sexo.toString() : null;
	}
	
	/**
	 * ORADB FUNCTION AELC_GET_ORIGEM_IG
	 * Busca a origem da Solicitação do Exame para IG
	 * @param solicitacaoExame
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String obterOrigemIg(AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException{

		String variavelTemInternacao = "N" ;
		String variavelTemAtendimentoUrgencia ="N" ;
		String variavelTemConsulta = "N" ;
		String variavelPacienteExterno = "N";
		String variavelTemCirurgia = "N";
	    
	    final AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		
		if(atendimento != null){
			
			if(atendimento.getInternacao() != null){
				variavelTemInternacao = "S";
			}
			
			if(atendimento.getAtendimentoUrgencia() != null){
				variavelTemAtendimentoUrgencia = "S";
			}
			
			if(atendimento.getConsulta() != null){
				variavelTemConsulta = "S";
			}
			/*
			  if v_esp_seq  is null then
                v_paciente_externo := 'S';
              end if;
            */
			if(atendimento. getEspecialidade()  == null){
				variavelPacienteExterno = "S";
			}
			
			List<MbcCirurgias> listaCirurgias = this.getBlocoCirurgicoFacade().pesquisarCirurgiaPorAtendimento(atendimento.getSeq());
			
			if(listaCirurgias != null && !listaCirurgias.isEmpty()){
				variavelTemCirurgia = "S";
			}
			
			final String v_combinacao = variavelPacienteExterno + variavelTemCirurgia + variavelTemAtendimentoUrgencia + variavelTemConsulta + variavelTemInternacao;

			AghParametros parametroUnidadeSida = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SIDA);
			BigDecimal valorUnidadeSida = parametroUnidadeSida.getVlrNumerico();
			
			if("S".equals(variavelTemInternacao) && valorUnidadeSida != null && atendimento.getUnidadeFuncional().getSeq().equals(valorUnidadeSida.intValue())){ // TODO CRIAR PARAMETRO
				return AMB;
			}

			if("S".equals(variavelTemInternacao) && Arrays.asList("NNSSS", "NSSSS", "NSNSS").contains(v_combinacao)) {
				
				Date v_dt_internacao = atendimento.getInternacao().getDthrInternacao();
				Date dataSolicitacao = solicitacaoExame.getCriadoEm();
				
				if(DateValidator.validaDataMenor(v_dt_internacao, dataSolicitacao)){
					return AMB;
				} else{
					return "INT";
				}
				
			}

			if(Arrays.asList("NNNSN","NNSSN","SNNNN","NSNNN","NSNSN","NSSSN").contains(v_combinacao)) {
				return AMB;
			} else if(Arrays.asList("NSNNS","NNNNS").contains(v_combinacao)){
				return "INT";
			} else{
				return AMB;
			}
			
		} else{
			// É Atendimento Diverso!
			return AMB;
		}
		
	}

	/*
	 * Getters para dependências
	 */
	
	protected LwsComunicacaoDAO getLwsComunicacaoDAO(){
		return lwsComunicacaoDAO;
	}
	
	protected LwsComunicacaoRN getLwsComunicacaoRN(){
		return lwsComunicacaoRN;
	}
	
	protected LwsComPacienteDAO getLwsComPacienteDAO(){
		return lwsComPacienteDAO;
	}
	
	protected LwsComAmostraDAO getLwsComAmostraDAO(){
		return lwsComAmostraDAO;
	}
	
	protected LwsComSolicitacaoExameDAO getLwsComSolicitacaoExameDAO(){
		return lwsComSolicitacaoExameDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}
	
	protected RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON(){
		return relatorioTicketExamesPacienteON;
	}
			
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
}