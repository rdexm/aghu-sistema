package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioCoresSinaleiro;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.DispensacaoMdtosCodBarrasON.DispensacaoMdtosCodBarrasONExceptionCode;
import br.gov.mec.aghu.farmacia.vo.ListaOcorrenciaVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class TratarOcorrenciasON extends BaseBusiness implements Serializable {


@EJB
private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;

@EJB
private TratarOcorrenciasRN tratarOcorrenciasRN;

private static final Log LOG = LogFactory.getLog(TratarOcorrenciasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IAdministracaoFacade administracaoFacade;

@Inject
private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4248941051338462109L;

	public enum TratarOcorrenciasONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_UNF_OU_OCORRENCIA_OBRIGATORIO,
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}
	
	public List<AfaDispensacaoMdtos> recuperarlistaOcorrenciasMdtosDispensados(AghUnidadesFuncionais unidadeFuncional, Date data,
			AfaTipoOcorDispensacao tipoOcorDispensacao, Integer prontuario, AinLeitos leito, AghUnidadesFuncionais farmacia) throws ApplicationBusinessException {
		
		if(unidadeFuncional== null && tipoOcorDispensacao ==null && prontuario==null){
			throw new ApplicationBusinessException(TratarOcorrenciasONExceptionCode.MENSAGEM_UNF_OU_OCORRENCIA_OBRIGATORIO);
		}
		BigDecimal qtdeDispensada = BigDecimal.ZERO;
		
		List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados = getAfaDispensacaoMdtosDAO()
		.pesquisarOcorrenciasMdtosDispensados(unidadeFuncional, data, tipoOcorDispensacao, prontuario, leito, farmacia, qtdeDispensada, DominioSituacaoItemPrescritoDispensacaoMdto.EG,   
				DominioSituacaoDispensacaoMdto.T, DominioSituacaoDispensacaoMdto.D);		
		
 
		for(AfaDispensacaoMdtos dispMdto:listaOcorrenciasMdtosDispensados){
			
			getAfaDispensacaoMdtosDAO().refresh(dispMdto);
			
			dispMdto.setCorSinaleiro(getAfaDispMdtoCbSpsRN().preencheSinaleira(dispMdto.getSeq(), dispMdto.getQtdeDispensada()));
			dispMdto.setItemDispensadoSemEtiqueta(getAfaDispMdtoCbSpsRN().assinalaMedicamentoDispensado(dispMdto.getSeq()));
			
			dispMdto.setLocalProntuarioMdto(
					dispMdto.getAtendimento().getAincLocalInt() + 
					dispMdto.getAtendimento().getPaciente().getProntuario().toString() +
					dispMdto.getMedicamento().getDescricaoEditada()); 
			
			dispMdto.setMdtoNomeProntuario(
					dispMdto.getMedicamento().getDescricaoEditada() + 
					dispMdto.getAtendimento().getPaciente().getNome() + 
					dispMdto.getAtendimento().getPaciente().getProntuario().toString()); 
			
			dispMdto.setApresNomeMdtoProntuario(
					dispMdto.getMedicamento().getTipoApresentacaoMedicamento().getSigla() + 
					dispMdto.getAtendimento().getPaciente().getNome() + 
					dispMdto.getMedicamento().getDescricaoEditada() +
					dispMdto.getAtendimento().getPaciente().getProntuario().toString()); 
			
			dispMdto.setOcorNomeProntuarioMdto(
					(dispMdto.getTipoOcorrenciaDispensacao()!= null ? dispMdto.getTipoOcorrenciaDispensacao().getSeqEDescricao() : "" )+ 
					dispMdto.getAtendimento().getPaciente().getNome() + 
					dispMdto.getAtendimento().getPaciente().getProntuario().toString() +
					(dispMdto.getMedicamento().getTipoApresentacaoMedicamento() != null ? dispMdto.getMedicamento().getTipoApresentacaoMedicamento().getSigla() : "")); 
			
			dispMdto.setFarmaciaNomeProntuarioMdto(
					dispMdto.getUnidadeFuncional().getUnidadeDescricao() + 
					dispMdto.getAtendimento().getPaciente().getNome() + 
					dispMdto.getAtendimento().getPaciente().getProntuario().toString() +
					dispMdto.getMedicamento().getTipoApresentacaoMedicamento().getSigla());
			
			dispMdto.setPermiteDispensacaoSemEtiqueta(verificarPermissaoDispensacaoSemEtiqueta(dispMdto));
		}		
		
		CoreUtil.ordenarLista(listaOcorrenciasMdtosDispensados, "medicamento.descricao", false);
		CoreUtil.ordenarLista(listaOcorrenciasMdtosDispensados, "corSinaleiro.codigo", true);
		
		return listaOcorrenciasMdtosDispensados;
	}
	
	public List<ListaOcorrenciaVO> recuperarRelatorioListaOcorrencia(String unidade, String dtReferencia, String ocorrencia, String unidFarmacia, Boolean unidPsiquiatrica) throws ApplicationBusinessException {
		
		BigDecimal qtdeDispensada = BigDecimal.ZERO;
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date data = null;
		try {
			data = sdf.parse(dtReferencia);
		} catch (ParseException e) {
			
			logError(e);
		}
		
		Short seqUnidadeFuncional = Short.valueOf(unidade); 
		 
		Short seqOcorrencia = Short.valueOf(ocorrencia); 
		
		Short seqFarmacia = null;
		if (unidFarmacia != null && !unidFarmacia.isEmpty()){
			seqFarmacia = Short.valueOf(unidFarmacia); 
		} 	

			
		List<ListaOcorrenciaVO> listaOcorrencia = getAfaDispensacaoMdtosDAO()
				.recuperarListaOcorrencia(seqUnidadeFuncional, data, seqOcorrencia, qtdeDispensada, seqFarmacia, DominioSituacaoItemPrescritoDispensacaoMdto.EG,   
						DominioSituacaoDispensacaoMdto.T, DominioSituacaoDispensacaoMdto.S);	
		
		//List<ListaOcorrenciaVO> listaVOs = new ArrayList<ListaOcorrenciaVO>();
		
		for (ListaOcorrenciaVO ocorrenciaVO: listaOcorrencia){
			
			//Formata o campo Descricao02 - Ocorrência/Data Triagem e seta				
			
			StringBuffer ocorrenciaDataTriagemFormat = new StringBuffer("OCORRÊNCIA: ")					
			.append(ocorrenciaVO.getDescricaoOcorrencia()) 
			.append(" - DATA DA TRIAGEM: ").append(DateUtil.obterDataFormatada(data, "dd/MM/yy"));
			ocorrenciaVO.setDescricao02(ocorrenciaDataTriagemFormat.toString());
			
			//Formata o campo Descricao03 - Medicamento/Concentracão/Descrição e seta			
			if (ocorrenciaVO.getConcentracao() == null){
				ocorrenciaVO.setDescricao03(ocorrenciaVO.getDescricaoMedicamento());
			}else{
				StringBuffer medConcDescricaoFormat = new StringBuffer(ocorrenciaVO.getDescricaoMedicamento()) 
				.append(' ').append(ocorrenciaVO.getConcentracao()) 
				.append(' ').append(ocorrenciaVO.getDescricaoUnidMedMedicas()); 
				ocorrenciaVO.setDescricao03(medConcDescricaoFormat.toString());
			}
			
			//Formata o campo Descricao04 - Andar/Ala/Descricao e seta			
			if (StringUtils.isNotBlank(ocorrenciaVO.getAndar()) && StringUtils.isNotBlank(ocorrenciaVO.getAndar())){
				StringBuffer andarAlaFormat = new StringBuffer(ocorrenciaVO.getAndar()) 
				.append(' ').append(ocorrenciaVO.getIndAla()) 
				.append(' ').append(ocorrenciaVO.getDescricaoUnidSolicitante()); 
				ocorrenciaVO.setDescricao04(andarAlaFormat.toString());
			}else{
				StringBuffer andarAlaFormat = new StringBuffer(ocorrenciaVO.getSiglaUnidSolicitante())
				.append(' ').append(ocorrenciaVO.getDescricaoUnidSolicitante()); 
				ocorrenciaVO.setDescricao04(andarAlaFormat.toString());
			}
			
			
			//Seta QtdeDispensada1 = Quantidade Dispensada1 - Quantidade Estornada			
						
			if(ocorrenciaVO.getQtdeEstornada() != null){

				BigDecimal valor = BigDecimal.ZERO;				
				valor = valor.add(ocorrenciaVO.getQtdeDispensada1().subtract(ocorrenciaVO.getQtdeEstornada()));				
				ocorrenciaVO.setQtdeDispensada1(valor);		
			}
				
				
			//Seta QtdeDispensada = QtdeDispensada1 || siglaApresMed
				
			String qtdisp = ocorrenciaVO.getQtdeDispensada1().toString();				
			StringBuffer QtdeDispensadaFormat = new StringBuffer(qtdisp).append(' ').append(ocorrenciaVO.getSiglaApresMed()); 				
			ocorrenciaVO.setQtdeDispensada(QtdeDispensadaFormat.toString());
			 
						
			//Seta o Local
			
			if(ocorrenciaVO.getSeqAtd() != null){
				//Integer seqAtd = Integer.valueOf(ocorrenciaVO.getSeqAtd()); 
				AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentosPorSeq(ocorrenciaVO.getSeqAtd());
				ocorrenciaVO.setLocal(getPrescricaoMedicaFacade().buscarResumoLocalPaciente(atendimento));
			}		
		
		}
		
		if (!(listaOcorrencia.isEmpty())) { 

			if(unidPsiquiatrica){					
				CoreUtil.ordenarLista(listaOcorrencia, "descricao03", true); // medicamento
				CoreUtil.ordenarLista(listaOcorrencia, "local", false);
				CoreUtil.ordenarLista(listaOcorrencia, "nome", true);					
			}else{
				CoreUtil.ordenarLista(listaOcorrencia, "prontuario", true);
				CoreUtil.ordenarLista(listaOcorrencia, "local", false);
				CoreUtil.ordenarLista(listaOcorrencia, "descricao03", true); // medicamento

				//AghuUtil.ordenarLista(listaOcorrencia, "descricao03", true);
				//AghuUtil.ordenarLista(listaOcorrencia, "local", true);
				//AghuUtil.ordenarLista(listaOcorrencia, "prontuario", true);
			}
		}
		
		return listaOcorrencia;		
		
	}

	public void assinaDispMdtoSemUsoDeEtiqueta(AfaDispensacaoMdtos admNew, AghMicrocomputador micro, AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException {
		validaMicroCadastrado(admNew, micro);
		validaSeMicroComputadorDispensador(admNew.getUnidadeFuncional(), admNew, microDispensador);
		getEstoqueFacade().tratarDispensacaoMedicamentoEstoque(admNew, null, nomeMicrocomputador);
		getAfaDispMdtoCbSpsRN().assinaDispMdtoSemUsoDeEtiqueta(admNew, microDispensador.getNome());
		admNew.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
	}
	
	public void validaMicroCadastrado(AfaDispensacaoMdtos admNew, AghMicrocomputador micro) throws ApplicationBusinessException{
		if(micro==null){
			recuperaSeDispensadoComEtiquetaAnterior(admNew);
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.MICROCOMPUTADOR_NAO_CADASTRADO_IDENTIFICADO);
		}
	}

	public void pesquisaEtiquetaComCbMedicamento(String etiqueta,
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados,
			AghUnidadesFuncionais farmacia, AghMicrocomputador micro, 
			AghMicrocomputador microDispensador) throws BaseException {
	
		validaMicroCadastrado(null, micro);
		validaSeMicroComputadorDispensador(farmacia, null, microDispensador);

		getTratarOcorrenciasRN().pesquisarEtiquetaComCbMedicamento(etiqueta,
				listaOcorrenciasMdtosDispensados, microDispensador.getNome());

	}
	
	/**
	 * Verifica se MicroComputador é dispensador da unidade funcional passada via parâmetro.
	 * Caso null não verifica a unidade funcional.
	 * @param unidadeFuncional
	 * @param admNew 
	 * @throws ApplicationBusinessException
	 */
	public void validaSeMicroComputadorDispensador(AghUnidadesFuncionais unidadeFuncional, AfaDispensacaoMdtos admNew, AghMicrocomputador microUserDispensador) throws ApplicationBusinessException {
		if(microUserDispensador== null){
			recuperaSeDispensadoComEtiquetaAnterior(admNew);
			throw new ApplicationBusinessException(DispensacaoMdtosCodBarrasONExceptionCode.MICROCOMPUTADOR_NAO_DISPENSADOR);
		}

		if(unidadeFuncional != null && !unidadeFuncional.equals(microUserDispensador.getAghUnidadesFuncionais())){
			recuperaSeDispensadoComEtiquetaAnterior(admNew);
			throw new ApplicationBusinessException(DispensacaoMdtosCodBarrasONExceptionCode.UNF_COMPUTADOR_DIFERE_UNF_MEDICAMENTO);
		}
	}
		
	private void recuperaSeDispensadoComEtiquetaAnterior(AfaDispensacaoMdtos admNew) {
		if(admNew != null){//Significa que a dispensação é sem etiqueta
			admNew.setItemDispensadoSemEtiqueta(!admNew.getItemDispensadoSemEtiqueta());
		}
	}

	public void processaCorSinaleiroPosAtualizacao(
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados) {
		for (AfaDispensacaoMdtos dispMdto : listaOcorrenciasMdtosDispensados) {
			dispMdto.setCorSinaleiro(getAfaDispMdtoCbSpsRN().preencheSinaleira(
					dispMdto.getSeq(), dispMdto.getQtdeDispensada()));
			dispMdto.setPermiteDispensacaoSemEtiqueta(verificarPermissaoDispensacaoSemEtiqueta(dispMdto));
		}
	}
	
	/**
	 * Quando PERMITE a dispensação sem etiqueta o retorno será TRUE #19065
	 * 
	 * @param dispMdto
	 * @return
	 */
	public Boolean verificarPermissaoDispensacaoSemEtiqueta(
			AfaDispensacaoMdtos dispMdto) {
		if (dispMdto.getItemDispensadoSemEtiqueta()) {
			return Boolean.TRUE;
		} else if (DominioCoresSinaleiro.AMARELO.equals(dispMdto
				.getCorSinaleiro())
				|| DominioCoresSinaleiro.VERDE.equals(dispMdto
						.getCorSinaleiro())) {
			return Boolean.FALSE;
		} else {
			Long qtdeEtiquetas = getAfaDispMdtoCbSpsDAO()
					.getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(
							dispMdto.getSeq(), null);
			if (qtdeEtiquetas == 0) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
	}
	
	public void recuperarRelatorioListaOcorrenciaCount(String unidade, String dtReferencia, String ocorrencia, String unidFarmacia) throws ApplicationBusinessException{
		
		BigDecimal qtdeDispensada = BigDecimal.ZERO;
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date data = null;
		try {
			data = sdf.parse(dtReferencia);
		} catch (ParseException e) {
			
			logError(e);
		}
		
		Short seqUnidadeFuncional = Short.valueOf(unidade); 
		 
		Short seqOcorrencia = Short.valueOf(ocorrencia); 
		
		Short seqFarmacia = null;		
		if (unidFarmacia != null && !unidFarmacia.isEmpty()){
			seqFarmacia = Short.valueOf(unidFarmacia); 
		}			
		
		Long count = getAfaDispensacaoMdtosDAO().recuperarListaOcorrenciaCount(
				seqUnidadeFuncional, data, seqOcorrencia, qtdeDispensada, seqFarmacia,
				DominioSituacaoItemPrescritoDispensacaoMdto.EG,   
				DominioSituacaoDispensacaoMdto.T, DominioSituacaoDispensacaoMdto.S);
		
		if(count == null || count.intValue() == 0){
			throw new ApplicationBusinessException(TratarOcorrenciasONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}			
	}
	
	
	//Getters

	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN() {
		return afaDispMdtoCbSpsRN;
	}
	
	private TratarOcorrenciasRN getTratarOcorrenciasRN() {
		return tratarOcorrenciasRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	public AghUnidadesFuncionais getFarmaciaMicroComputador(
			AghMicrocomputador microUser, String computadorNomeRede) throws ApplicationBusinessException {
		if(microUser == null){
			microUser = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(computadorNomeRede, DominioCaracteristicaMicrocomputador.DISPENSADOR);
		}
		if(microUser==null || microUser.getAghUnidadesFuncionais() == null){
			return null;
		}
		Boolean unfFarmacia = getAghuFacade().unidadeFuncionalPossuiCaracteristica(microUser.getAghUnidadesFuncionais().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
		if(unfFarmacia){
			return microUser.getAghUnidadesFuncionais();
		}else{
			return null;
		}
	}
	
	private AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO() {
		return afaDispMdtoCbSpsDAO;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}	
	
}