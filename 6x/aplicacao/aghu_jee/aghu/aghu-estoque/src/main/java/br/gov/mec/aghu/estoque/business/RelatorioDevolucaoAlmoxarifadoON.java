package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioDevolucaoAlmoxarifadoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(RelatorioDevolucaoAlmoxarifadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO;
	
	private static final long serialVersionUID = -2258091248426431825L;

	private enum RelatorioDevolucaoAlmoxarifadoEmun {
		SEPARADOR(";"),
		ENCODE("ISO-8859-1"),
		CONTENT_TYPE("text/csv"),
		EXTENSAO(".csv"),
		QUEBRA_LINHA("\n"),
		NOME_ARQUIVO("Imprimir_Devolucao_Almoxarifado_SEQ_");
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private String value;
		
		private RelatorioDevolucaoAlmoxarifadoEmun(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected SceDevolucaoAlmoxarifadoDAO getDevolucaoAlmoxarifadoDAO() {
		return sceDevolucaoAlmoxarifadoDAO;
	}
	
	
	/**
	 * 
	 * @param numeroDevAlmox
	 * @return
	 */
	public List<RelatorioDevolucaoAlmoxarifadoVO> gerarDadosRelatorioDevolucaoAlmoxarifado(Integer numeroDevAlmox) {		
		return getDevolucaoAlmoxarifadoDAO().pesquisarDadosRelatorioDevolucaoAlmoxarifado(numeroDevAlmox);
	}

	private File getFile(Integer numeroDevolAlmox) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append(RelatorioDevolucaoAlmoxarifadoEmun.NOME_ARQUIVO.toString())
		.append(numeroDevolAlmox);
		
		return File.createTempFile(sb.toString(), RelatorioDevolucaoAlmoxarifadoEmun.EXTENSAO.toString());
	}

	public String gerarCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDevolAlmox, List<RelatorioDevolucaoAlmoxarifadoVO> dados, Map<String, Object> parametros) throws IOException {
		File file = getFile(numeroDevolAlmox);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), RelatorioDevolucaoAlmoxarifadoEmun.ENCODE.toString());
		
		// formata campos data
		String dtGeracaoStr = "";
		if(parametros.get("dtGeracao") != null) {
			Date dtGeracao = (Date)parametros.get("dtGeracao");
			dtGeracaoStr = DateUtil.dataToString(dtGeracao, "dd/MM/yyyy");
		}
				
		out.write(new StringBuilder()
			.append("SEQ").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append("GERADA_EM").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append("ALMOX").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append("CENTRO_CUSTOS").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append("DIGITADO_POR").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("NUMERO_RAMAL").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append("OBSERVACAO").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString()) 
			.append(RelatorioDevolucaoAlmoxarifadoEmun.QUEBRA_LINHA.toString())
			.toString());
		
		out.write(new StringBuilder()
			.append(this.transformarNuloEmBranco(parametros.get("seq"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(dtGeracaoStr)).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(parametros.get("almSeq"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(parametros.get("centroCustoSeqDescricao"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(parametros.get("nomePessoaServidor"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(parametros.get("ramNroRamal"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(this.transformarNuloEmBranco(parametros.get("observacao"))).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(RelatorioDevolucaoAlmoxarifadoEmun.QUEBRA_LINHA.toString())
			.toString());

		// itens
		out.write(new StringBuilder()
			.append("MATERIAL").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("NOME").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("UNID").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("END").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("QTDE_RECEBIDA").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append("VALOR_RECEBIDO").append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
			.append(RelatorioDevolucaoAlmoxarifadoEmun.QUEBRA_LINHA.toString())
			.toString());
		
		for(RelatorioDevolucaoAlmoxarifadoVO obj : dados) {
			out.write(new StringBuilder()
				.append(this.transformarNuloEmBranco(obj.getMatCodigo())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(this.transformarNuloEmBranco(obj.getMatNome())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(this.transformarNuloEmBranco(obj.getUmdCodigo())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(this.transformarNuloEmBranco(obj.getEndereco())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(this.transformarNuloEmBranco(obj.getQuantidade())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(this.transformarNuloEmBranco(obj.getValor())).append(RelatorioDevolucaoAlmoxarifadoEmun.SEPARADOR.toString())
				.append(RelatorioDevolucaoAlmoxarifadoEmun.QUEBRA_LINHA.toString())
				.toString());	
		}
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}
	
	
	/**
	 * Utilizado na exportação de CVS
	 * Converte um valor nulo no valor em branco.
	 * @param valor
	 * @return
	 */
	private String transformarNuloEmBranco(Object valor){
		
		String retorno = "";
		
		if(valor != null && StringUtils.isNotEmpty(valor.toString())){
			retorno = valor.toString().trim();
		}
		
		return retorno;
	}

	public String nameHeaderEfetuarDownloadCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDA)  {
		return RelatorioDevolucaoAlmoxarifadoEmun.NOME_ARQUIVO.toString() + numeroDA + RelatorioDevolucaoAlmoxarifadoEmun.EXTENSAO.toString();
	}	
	
}
